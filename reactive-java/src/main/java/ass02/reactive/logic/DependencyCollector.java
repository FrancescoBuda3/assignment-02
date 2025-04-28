package ass02.reactive.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class DependencyCollector extends VoidVisitorAdapter<Object> {
    private List<String> classNames;
    private ClassType classType;
    private String packageName;
    private List<String> imports;
    private List<String> types;

    public DependencyCollector() {
        this.classNames = new ArrayList<>();
        this.packageName = "";
        this.imports = new ArrayList<>();
        this.types = new ArrayList<>();
    }

    public DependencyInfo getInfos() {
        return new DependencyInfo(
                this.getClassName(),
                this.classType,
                this.packageName,
                this.getDependencies());
    }

    private String getClassName() {
        String className = "...";
        if (this.classNames.size() > 0) {
            className = this.classNames.getLast();
        }
        return className;
    }

    private List<String> getDependencies() {
        List<String> dependencies = new ArrayList<>();
        dependencies.addAll(this.imports);
        Set<String> bareImports = this.imports.stream()
                .map(i -> i.substring(i.lastIndexOf(".") + 1))
                .collect(Collectors.toSet());
        for (String type : this.types) {
            if (!bareImports.contains(type)) {
                dependencies.add(type);
            }
        }
        return dependencies;
    }

    private void addType(String type) {
        if (type.contains("<")) {
            this.addType(type.substring(0, type.indexOf("<")));
            this.addType(type.substring(type.indexOf("<") + 1, type.length() - 1));
        } else if (type.contains(",")) {
            type = type.replaceAll("\\s+", "");
            String[] types = type.split(",");
            for (String t : types) {
                this.addType(t);
            }
        } else if (type.contains("[")) {
            type = type.substring(0, type.indexOf("["));
            this.addType(type);
        } else if (!type.equals("") && !this.types.contains(type)) {
            if (type.contains("[]")) {
                type = type.substring(0, type.indexOf("[]"));
            }
            this.types.add(type);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        this.classNames.add(n.getNameAsString());
        this.classType = ClassType.CLASS;
        if (n.isAbstract()) {
            this.classType = ClassType.ABSTRACT_CLASS;
        } else if (n.isInterface()) {
            this.classType = ClassType.INTERFACE;
        }
        n.getExtendedTypes().forEach(t -> this.addType(t.getNameAsString()));
        n.getImplementedTypes().forEach(t -> this.addType(t.getNameAsString()));
    }

    @Override
    public void visit(EnumDeclaration n, Object arg) {
        super.visit(n, arg);
        this.classNames.add(n.getNameAsString());
        this.classType = ClassType.ENUM;
        n.getImplementedTypes().forEach(t -> this.addType(t.getNameAsString()));
    }

    @Override
    public void visit(PackageDeclaration n, Object arg) {
        super.visit(n, arg);
        this.packageName = n.getNameAsString();
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        this.imports.add(n.getNameAsString());
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
        n.getVariables().forEach(v -> this.addType(v.getTypeAsString()));
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        this.addType(n.getTypeAsString());
        n.getParameters().forEach(p -> this.addType(p.getTypeAsString()));
    }

    @Override
    public void visit(ObjectCreationExpr n, Object arg) {
        super.visit(n, arg);
        this.addType(n.getTypeAsString());
    }

    @Override
    public void visit(VariableDeclarator n, Object arg) {
        super.visit(n, arg);
        this.addType(n.getTypeAsString());
    }

    @Override
    public void visit(TypeParameter n, Object arg) {
        super.visit(n, arg);
        this.addType(n.getNameAsString());
    }
}
