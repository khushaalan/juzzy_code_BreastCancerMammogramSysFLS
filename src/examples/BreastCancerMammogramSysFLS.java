package examples;

import generic.*;
import tools.*;
import type1.sets.*;
import type1.system.*;


public class BreastCancerMammogramSysFLS {
	
	Input calcification,mass;	Output impression;	T1_Rulebase rulebase;

    public BreastCancerMammogramSysFLS() {
    calcification = new Input("Calcification", new Tuple(0,10));    
    mass= new Input("Mass", new Tuple(0,30)); 						
    impression = new Output("Impression", new Tuple(-20,20));     

    /*
    T1MF_Triangular lessMF = new T1MF_Triangular("MF for less", 0,2,4) ;
    T1MF_Triangular moreMF = new T1MF_Triangular("MF for more",6,8,10);
    */
    double arr_lessMF[]= {0,0,2,4};
    T1MF_Trapezoidal lessMF = new T1MF_Trapezoidal("LESS", arr_lessMF) ;
    T1MF_Triangular severalMF = new T1MF_Triangular("SEVERAL",2, 5, 8);
    double arr_moreMF[]={6,8,10,10};
    T1MF_Trapezoidal moreMF = new T1MF_Trapezoidal("MORE",arr_moreMF);

    /*
     T1MF_Triangular smallMF = new T1MF_Triangular("MF for small",0,5,10);
     T1MF_Triangular largeMF = new T1MF_Triangular("MF for large",17,25,30);
    */
    double[]arr_smallMF= {0,0,5,12};
    T1MF_Trapezoidal smallMF = new T1MF_Trapezoidal("SMALL",arr_smallMF);
    T1MF_Triangular mediumMF = new T1MF_Triangular("MEDIUM",5, 15, 25);
    double[]arr_largeMF = {17,25,30,30};
    T1MF_Trapezoidal largeMF = new T1MF_Trapezoidal("LARGE",arr_largeMF);
    
    
    T1MF_Triangular benignMF = new T1MF_Triangular("BENIGN",-20, -10, 0);
    T1MF_Triangular suspiciousMalignantMF = new T1MF_Triangular("SUSPICIOUS MALIGNANT",-10, 0, 10);
    T1MF_Triangular malignantMF = new T1MF_Triangular("MALIGNANT",0, 10, 20);


    T1_Antecedent less = new T1_Antecedent("Less",lessMF, calcification);
    T1_Antecedent several = new T1_Antecedent("Several",severalMF, calcification);
    T1_Antecedent more = new T1_Antecedent("More",moreMF, calcification);
    
    T1_Antecedent small = new T1_Antecedent("Small",smallMF, mass);
    T1_Antecedent medium = new T1_Antecedent("Medium",mediumMF, mass);
    T1_Antecedent large = new T1_Antecedent("Large",largeMF, mass);

    T1_Consequent benign = new T1_Consequent("Benign", benignMF, impression);
    T1_Consequent suspiciousMalignant = new T1_Consequent("Suspicious malignant", suspiciousMalignantMF, impression);
    T1_Consequent malignant = new T1_Consequent("Malignant", malignantMF, impression);

   
    
    rulebase = new T1_Rulebase(9);
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{less, small}, benign));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{less, medium}, benign));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{less, large}, benign));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{several, small}, suspiciousMalignant));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{several, medium}, suspiciousMalignant));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{several, large}, suspiciousMalignant));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{more, small}, malignant));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{more, medium}, malignant));
    rulebase.addRule(new T1_Rule(new T1_Antecedent[]{more, large}, malignant));
    
    
    impression.setDiscretisationLevel(50);        
    

    getImpression(7,15);
    
    plotMFs("Calcification Membership Functions", new T1MF_Interface[]{lessMF, severalMF, moreMF}, calcification.getDomain(), 100); 
    plotMFs("Mass Membership Functions", new T1MF_Interface[]{smallMF, mediumMF, largeMF}, mass.getDomain(), 100);
    plotMFs("Impression Membership Functions", new T1MF_Interface[]{benignMF, suspiciousMalignantMF, malignantMF}, impression.getDomain(), 100);
   
    plotControlSurface(true, 100, 100);

    System.out.println("\n"+rulebase);        
}

    private void getImpression(int Calcification, int Mass){
    //first, set the inputs
    calcification.setInput(Calcification);
    mass.setInput(Mass);
    //now execute the FLS and print output
    System.out.println("The calcification was: "+calcification.getInput());
    System.out.println("The mass was: "+mass.getInput());
    System.out.println("Using height defuzzification, the FLS suggests a impression of: "
            + rulebase.evaluate(0).get(impression)); 
    System.out.println("Using centroid defuzzification, the FLS suggests a impression of: "
            + rulebase.evaluate(1).get(impression));     
}

    private void plotMFs(String name, T1MF_Interface[] sets, Tuple xAxisRange, int discretizationLevel){
    JMathPlotter plotter = new JMathPlotter(17,17,15);
    for (int i=0;i<sets.length;i++)
    {
        plotter.plotMF(sets[i].getName(), sets[i], discretizationLevel, xAxisRange, new Tuple(0.0,1.0), false);
    }
    plotter.show(name);
    }

    private void plotControlSurface(boolean useCentroidDefuzzification, int input1Discs, int input2Discs){
    double output;
    double[] x = new double[input1Discs];
    double[] y = new double[input2Discs];
    double[][] z = new double[y.length][x.length];
    double incrX, incrY;
    incrX = calcification.getDomain().getSize()/(input1Discs-1.0);
    incrY = mass.getDomain().getSize()/(input2Discs-1.0);

    
    for(int currentX=0; currentX<input1Discs; currentX++)
    {
        x[currentX] = currentX * incrX;        
    }
    for(int currentY=0; currentY<input2Discs; currentY++)
    {
        y[currentY] = currentY * incrY;
    }
    
    for(int currentX=0; currentX<input1Discs; currentX++)
    {
        calcification.setInput(x[currentX]);
        for(int currentY=0; currentY<input2Discs; currentY++)
        {
            mass.setInput(y[currentY]);
            if(useCentroidDefuzzification)
                output = rulebase.evaluate(1).get(impression);
            else
                output = rulebase.evaluate(0).get(impression);
            z[currentY][currentX] = output;
        }    
    }
    
    //now do the plotting
    JMathPlotter plotter = new JMathPlotter(17, 17, 14);
    plotter.plotControlSurface("Control Surface",
            new String[]{calcification.getName(), mass.getName(), "Impression"}, x, y, z, new Tuple(0.0,30.0), true);   
    plotter.show("Type-1 Fuzzy Logic System Control Surface for Breast Cancer Mammogram System");
    }

    public static void main (String args[]){
    	new BreastCancerMammogramSysFLS();
    }

}
