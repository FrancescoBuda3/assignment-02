package ass02.reactive.logic;

public enum ClassType {
    CLASS, INTERFACE, ENUM, ABSTRACT_CLASS;

    @Override
    public String toString() {
        switch (this) {
            case CLASS:
                return "Class";
            case INTERFACE:
                return "Interface";
            case ENUM:
                return "Enum";
            case ABSTRACT_CLASS:
                return "Abstract Class";
            default:
                return super.toString();
        }
    }
}
