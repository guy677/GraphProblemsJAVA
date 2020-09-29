package NetWork.TaskManager;

public enum TaskType {
    UNKNOWN(3){
        public String toString(){
            return "UNKNOWN "+"Priority: "+ getPriority();
        }
    },
    COMPUTATIONAL(2){
        public String toString(){
            return "COMPUTATIONAL "+"Priority: "+ getPriority();
        }
    },
    IO(1) {
        public String toString() {
            return "IO "+"Priority: "+ getPriority();
        }
    };
    private int priority;
    TaskType(int priority) {
        setPriority(priority);
    }
    public int getPriority(){
        return this.priority;
    }
    public void setPriority(int oPriority){
        this.priority = oPriority;
    }
}
