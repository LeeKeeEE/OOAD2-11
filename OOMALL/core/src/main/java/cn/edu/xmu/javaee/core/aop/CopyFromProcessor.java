package cn.edu.xmu.javaee.core.aop;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import javax.annotation.processing.SupportedAnnotationTypes;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author huang zhong
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"cn.edu.xmu.javaee.core.aop.CopyFrom","cn.edu.xmu.javaee.core.aop.CopyFromOf","cn.edu.xmu.javaee.core.aop.CopyFromExclude"})
// 声明一个注解处理器，支持CopyFrom、CopyFromOf、CopyFromExclude注解
public class CopyFromProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    // 返回支持的Java源代码版本
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    // 初始化处理环境
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    // 处理注解
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty())
            return false;

        // 创建一个名为CloneFactory的类
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder("CloneFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        // 开始处理注解
        messager.printMessage(Diagnostic.Kind.NOTE, "CopyFromProcessor start");
        roundEnv.getElementsAnnotatedWith(CopyFrom.class).stream()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(element -> (TypeElement)element)
                .forEach(element -> {
                    // 打印处理的类名
                    messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName().toString());

                    // 获取所有setter方法
                    List<ExecutableElement> targetMethods = getAllMethods(element)
                            .stream()
                            .filter(method -> method.getParameters().size() == 1 && method.getSimpleName().toString().startsWith("set"))
                            .collect(Collectors.toList());
                    // 打印setter方法
                    messager.printMessage(Diagnostic.Kind.NOTE, targetMethods.toString());

                    // 获取源类
                    getSourceClass(element, "value",CopyFrom.class).forEach(sourceClass -> {
                        // 获取源类的所有getter方法
                        List<ExecutableElement> sourceMethods = getAllMethods((TypeElement)sourceClass.asElement())
                                .stream()
                                .filter(method -> method.getParameters().isEmpty() && !method.getReturnType().getKind().equals(TypeKind.VOID) && method.getSimpleName().toString().startsWith("get"))
                                .collect(Collectors.toList());
                        // 打印getter方法
                        messager.printMessage(Diagnostic.Kind.NOTE, sourceMethods.toString());

                        // 创建一个名为copy的方法
                        MethodSpec.Builder copyMethodBuilder = MethodSpec.methodBuilder("copy")
                                .addJavadoc("Copy all fields from source to target\n")
                                .addJavadoc("@param target the target object\n")
                                .addJavadoc("@param source the source object\n")
                                .addJavadoc("@return the copied target object\n")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(TypeName.get(element.asType()))
                                .addParameter(TypeName.get(element.asType()), "target")
                                .addParameter(TypeName.get(sourceClass), "source");

                        // 过滤出目标类的setter方法，并生成对应的copy语句
                        targetMethods.stream().filter(targetMethod -> {
                            String targetMethodName = targetMethod.getSimpleName().toString().substring(3);
                            List<DeclaredType> excludeList=getSourceClass(targetMethod,"value", CopyFrom.Exclude.class);
                            List<DeclaredType> ofList=getSourceClass(targetMethod,"value", CopyFrom.Of.class);

                            // 根据CopyFrom.Exclude和CopyFrom.Of的注解来过滤方法
                            if(!excludeList.isEmpty() && !ofList.isEmpty())
                            {
                                if((excludeList.stream().anyMatch(hasClass -> TypeName.get(hasClass).toString().equals(TypeName.get(sourceClass).toString())))
                                        || ofList.stream().noneMatch(hasClass-> TypeName.get(hasClass).toString().equals(TypeName.get(sourceClass).toString())))
                                {
                                    return false;
                                }
                            }else if(excludeList.isEmpty() && !ofList.isEmpty())
                            {
                                if(ofList.stream().noneMatch(hasClass-> TypeName.get(hasClass).toString().equals(TypeName.get(sourceClass).toString())))
                                {
                                    return false;
                                }
                            }else if(!excludeList.isEmpty() && ofList.isEmpty())
                            {
                                if(excludeList.stream().anyMatch(hasClass -> TypeName.get(hasClass).toString().equals(TypeName.get(sourceClass).toString())))
                                {
                                    return false;
                                }
                            }


                            return sourceMethods.stream().anyMatch(sourceMethod -> sourceMethod.getSimpleName().toString().substring(3).equals(targetMethodName));
                        }).map(method -> method.getSimpleName().toString().substring(3)).forEach(methodName -> {
                            copyMethodBuilder.addStatement("target.set" + methodName + "(source.get" + methodName + "())");
                        });

                        // 返回目标对象
                        copyMethodBuilder.addStatement("return target");

                        // 将copy方法添加到类中
                        typeSpecBuilder.addMethod(copyMethodBuilder.build());
                    });
                });

        // 创建Java文件
        JavaFile javaFile = JavaFile.builder("cn.edu.xmu.javaee.core.util", typeSpecBuilder.build()).build();

        // 将Java文件写入到文件中
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (Exception e) {
            // 如果出现异常，打印错误信息
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    // 获取类的所有方法
    private List<ExecutableElement> getAllMethods(TypeElement type) {
        return new ArrayList<>(ElementFilter.methodsIn(type.getEnclosedElements()));
    }

    // 获取元素的注解
    private Optional<AnnotationMirror> getAnnotationMirror(Element element, Class<?> clazz) {
        String clazzName = TypeName.get(clazz).toString();
        for(AnnotationMirror m : element.getAnnotationMirrors()) {
            if(m.getAnnotationType().toString().equals(clazzName)) {
                return Optional.ofNullable(m);
            }
        }
        return Optional.empty();
    }

    // 获取注解的值
    private Optional<AnnotationValue> getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for(Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
            if(entry.getKey().getSimpleName().toString().equals(key)) {
                messager.printMessage(Diagnostic.Kind.NOTE, String.format("Entry: %s, value: %s", entry.getKey().getSimpleName().toString(), entry.getValue().toString()));
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    // 获取注解的??，并转换为类型列表
    private List<DeclaredType> getSourceClass(Element clazz, String key,Class clas) {
        return getAnnotationMirror(clazz, clas)
                .flatMap(annotation -> getAnnotationValue(annotation, key))
                // ^ note that annotation value here corresponds to Class[],
                .map(annotation -> (List<AnnotationValue>)annotation.getValue())
                .map(fromClasses -> fromClasses.stream()
                        .map(fromClass -> (DeclaredType)fromClass.getValue())
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
}
