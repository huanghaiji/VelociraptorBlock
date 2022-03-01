package velociraptor.scheduling.flow;

public interface FlowBlockQuantitativePackage {

    /**
     * block流控制过程中的各种参数
     */
   default void setFlowBlockParameter(FlowBlockQuantitative parameter){
       System.out.println("NOT quantitative=parameter.");
   }

    FlowBlockQuantitative quantitative();

}