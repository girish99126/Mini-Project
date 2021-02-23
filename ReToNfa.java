import java.util.Scanner;
import java.util.Stack;
import java.util.ArrayList;
import java.util.*;


class ReToNfa
{
  
    
    public static String addConcatenation(String regex)
    {   
        
        String res="";
        ArrayList operators = new ArrayList();
        operators.add('+');
        operators.add('*');
        
        for(int i=0;i<regex.length();i++)
        {
            Character c1 = regex.charAt(i);
            if(i+1<regex.length())
            {
                Character c2=regex.charAt(i+1);
                res+=c1;
                if(!c1.equals('(') && !c2.equals(')')  && !c1.equals('+') && !c2.equals('*') && !c2.equals('+') &&  !c2.equals('.') && !c1.equals('.'))
                {
                    res+='.';
                }
            }
        }
        res += regex.charAt(regex.length()-1);
        return res;      
    }
    

    public  static int getPrecedence(String c){

        if(c.equals("(")){
            return 1;
        }
        else if(c.equals("+")){
            return 2;
            
        }
        else if(c.equals(".")){
            return 3;
        }
        else if(c.equals("*")){
            return 4;
        }
        else{
            return 6;
        }   

    }
    public static String Shunt(String regex)
    {
        regex = addConcatenation(regex);
        Stack stack = new Stack();
        String postfix="";
        
        for(int i=0;i<regex.length();i++)
        {   
          //  System.out.println(stack);
            Character token = regex.charAt(i);
             
            if(token.equals('(')){
                stack.push(token);
            }
            else if(token.equals(')')){
                while(!stack.peek().equals('('))
                {
                    postfix+=stack.pop();
        
                }
                stack.pop();
            }



            

            else if(token.equals('.') || token.equals('+') || token.equals('*') )
            {
                int token_precedence = getPrecedence(token.toString());
               
                if(stack.size()>0)
                {   
                     int peek_precedence = getPrecedence(stack.peek().toString());
                    
                    while((token_precedence <= peek_precedence) )
                    {   
                       
                        
                        postfix+=stack.pop();
                        if(stack.size()>0)
                        {
                            peek_precedence = getPrecedence(stack.peek().toString());

                        }
                        else
                            break;
                            
                    }
                } 
                stack.push(token);
            }
            else
                postfix+=token;        
            
        }
        while(stack.size()>0)
        {
            postfix+=stack.pop();
        }
    
        return postfix;
    }
}












class Transition
{
    Character symbol;
    NfaNode toState=null;
    Transition(Character symbol, NfaNode toState)
    {
        this.symbol=symbol;
        this.toState=toState;
    }
    void setTransition(Character symbol,NfaNode toState)
    {
        this.symbol=symbol;
        this.toState=toState;
    }
    void printTransition()
    {
        System.out.println(symbol+"-->"+toState.getLabel());
    }


}


class NfaNode
{
    int label=-1;
    ArrayList<Transition> transitions =  new ArrayList<Transition>();
    boolean isStart = false,isFinal = false;
    void addTransition(Character symbol,NfaNode toState)
    {
        Transition temp =  new Transition(symbol,toState);
        transitions.add(temp);
    }

    void setLabel(int label)
    {
        this.label = label;
    }
    int getLabel()
    {
        return label;
    }
}   


class Nfa
{   
    static int label_count=0; 
    NfaNode Start=null,Final=null;
    Nfa(){}
    Nfa(String postfix){
      
    }
    Nfa buildNfa(String postfix)
    {
        Stack<Nfa> stack = new Stack<Nfa>();
        for(int i=0;i<postfix.length();i++)
        {
            Character current=postfix.charAt(i);
            if(current=='+' || current=='.' || current=='*')
            {
                Nfa temp1,temp2;
                switch(current)
                {
                    
                    case '.':   
                        temp2=stack.pop();
                        temp1=stack.pop();
                        stack.push(this.concat(temp1,temp2));
                        break;
                    case '+':
                        temp2 = stack.pop();
                        temp1 = stack.pop();
                        stack.push(this.union(temp1,temp2));
                        break;
                    case '*':
                        temp1 =stack.pop();
                        stack.push(this.kleene(temp1));
                        break;

                }
            }
            else
            {
                stack.push(this.alphabet(current));
            }
        }    
        Nfa result = stack.pop();    
        return result;
    }
    Nfa alphabet(Character a)
    {
        Nfa temp = new Nfa();
        NfaNode temp1 = new NfaNode();
        NfaNode temp2 = new NfaNode();
        temp1.isStart = true;
        temp2.isFinal = true;
        temp1.addTransition(a, temp2);
        temp.Start = temp1;
        temp.Final = temp2;
        return temp;
        
    }


     Nfa union(Nfa a, Nfa b){

        Nfa temp = new Nfa();
        
        NfaNode newStart = new NfaNode();
        NfaNode newFinal = new NfaNode();
        newStart.isStart=true;
        newFinal.isFinal=true;

        newStart.addTransition('e',a.Start);

        newStart.addTransition('e', b.Start);
    
        a.Start.isStart=false;
        a.Final.isFinal= false;
        b.Start.isStart=false;
        b.Final.isFinal= false;
        a.Final.addTransition('e', newFinal);

        b.Final.addTransition('e', newFinal);

        a.Start =null;
        b.Final = null;
        a.Final = null;
        b.Start = null;

        temp.Start = newStart;
        temp.Final = newFinal;
        

        return temp;
        

    }

     Nfa concat(Nfa a,Nfa b)
    {
        Nfa temp = new Nfa();
        a.Final.addTransition('e', b.Start);
 
        temp.Start = a.Start;
        temp.Final = b.Final;
        a.Start=null;
        a.Final=null;
        b.Start=null;
        b.Final=null;
        return temp;
    }



    Nfa kleene(Nfa a){

        Nfa temp = new Nfa();
        
        NfaNode newStart = new NfaNode();
        NfaNode newFinal = new NfaNode();
        newStart.isStart=true;
        newFinal.isFinal=true;

        a.Start.isStart =false;
        a.Final.isFinal = false;
        newStart.addTransition('e', a.Start);
        newStart.addTransition('e',newFinal);
        a.Final.addTransition('e', newFinal);
        a.Final.addTransition('e', a.Start);
      
    
        a.Start =null;
        a.Final = null;

        temp.Start = newStart;
        temp.Final = newFinal;
        
        return temp;
        

    }
    /* 
    ArrayList<NfaNode> eclosure(NfaNode a,ArrayList<NfaNode> result)
    {
        result.add(a);
        Iterator<Transition> trans = a.transitions.iterator();
        while(trans.hasNext())
        {
            Transition tempTrans = trans.next();
            if(tempTrans.symbol=='e')
            {
                result.add(tempTrans.toState);
                result.addAll(eclosure(tempTrans.toState,result));

            }
        }
        return result;


    } */


    //  void Dfs(NfaNode start)
    // {
    //     Iterator<Transition> temp = start.transitions.iterator();
    //     while(temp.hasNext())
    //     {
    //         Transition tempTrans=temp.next();
    //         if(tempTrans.toState.label==-1)
    //             tempTrans.toState.setLabel(label_count++);
    //         System.out.println(start.label+"-->"+tempTrans.symbol+"-->"+tempTrans.toState.label);
    //         Dfs(tempTrans.toState);
    //     }

    // }  
    void Bfs(NfaNode start,ArrayList<NfaNode> visited)
    {
        
           Iterator<Transition> iter = start.transitions.iterator();
            Queue<NfaNode> queue = new LinkedList<NfaNode>();
            while(iter.hasNext())
            {
                Transition tempTrans = iter.next();
               if(tempTrans.toState.label==-1)
                    tempTrans.toState.setLabel(label_count++);
                queue.add(tempTrans.toState);
                System.out.println(start.getLabel() +"-->"+tempTrans.symbol+"-->"+tempTrans.toState.getLabel());
            }
            visited.add(start);
            while(queue.size()>0)
            {
                NfaNode tempNode=queue.remove();
                if(!visited.contains(tempNode))
                    Bfs(tempNode,visited);
            }
            

    }

    
    void Traverse(Nfa a)
    {
        NfaNode start=a.Start;
        start.setLabel(label_count++);
        ArrayList<NfaNode> visited = new ArrayList<NfaNode>();
        this.Bfs(start,visited);
       //this.Dfs(start);

        
        
    }
        
/*         
        while(start!=null)
        {
            ArrayList<Transition> trans = start.transitions;
            System.out.print("q"+start.label+"-->");
            
            for(int i=0;i<trans.size();i++)
            {
                System.out.print(trans.get(i).symbol+"-->"+"q"+trans.get(i).toState.label);
                
            }
            System.out.println();
            System.out.println();
            if(trans.size()!=0)
                start=trans.get(0).toState;
            else
               break;
        } */
        

    public static void main(String[] args)throws Exception {
        Nfa nfa=new Nfa();
        String postfix = ReToNfa.Shunt("(a+b)");

        Nfa result = nfa.buildNfa(postfix);
        result.Traverse(result);
       /*  ArrayList<NfaNode> temp2=new ArrayList<NfaNode>();
        Iterator<NfaNode> temp = result.eclosure(result.Start,temp2).iterator();
        while(temp.hasNext())
        {
            System.out.println(temp.next().label);
        }
        */


    }
    



}







       