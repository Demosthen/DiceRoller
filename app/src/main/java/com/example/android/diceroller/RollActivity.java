package com.example.android.diceroller;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class RollActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll);
        ArrayList<Button> numPad = new ArrayList<Button>(0);
        numPad.add((Button)findViewById(R.id.num0));
        numPad.add((Button)findViewById(R.id.num1));
        numPad.add((Button)findViewById(R.id.num2));
        numPad.add((Button)findViewById(R.id.num3));
        numPad.add((Button)findViewById(R.id.num4));
        numPad.add((Button)findViewById(R.id.num5));
        numPad.add((Button)findViewById(R.id.num6));
        numPad.add((Button)findViewById(R.id.num7));
        numPad.add((Button)findViewById(R.id.num8));
        numPad.add((Button)findViewById(R.id.num9));
        ArrayList<Button> ops = new ArrayList<Button>(0);
        ops.add((Button) findViewById(R.id.d4));
        ops.add((Button) findViewById(R.id.d6));
        ops.add((Button) findViewById(R.id.d8));
        ops.add((Button) findViewById(R.id.d10));
        ops.add((Button) findViewById(R.id.d12));
        ops.add((Button) findViewById(R.id.d20));
        ops.add((Button) findViewById(R.id.d100));
        Button minus = (Button) findViewById(R.id.minus);
        Button plus = (Button) findViewById(R.id.plus);
        Button roll = (Button) findViewById(R.id.roll);
        Button clear = (Button) findViewById(R.id.clear);
        ops.add(minus);
        ops.add(plus);
        final TextView exp = (TextView) findViewById(R.id.expression);
        final TextView total = (TextView) findViewById(R.id.total);
        roll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //set total equal to evaluated expression
                Pair eval = evaluate(exp.getText().toString());
                total.setText(Long.toString((Long)eval.first));
                total.setTag(eval.second);
            }
        });
        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                exp.setText("");
                total.setText("");
            }
        });
        for(int i=0; i<numPad.size(); i++){
            numPad.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exp.setText(exp.getText() + ((Button)view).getText().toString());
                }
            });
        }
        for(int i=0; i<ops.size(); i++){
            ops.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //set total equal to evaluated expression
                    exp.setText(exp.getText() + ((Button)view).getText().toString());
                }
            });
        }
        total.setOnClickListener(new View. OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(), (String) total.getTag(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item ){
        int id = item.getItemId();
        if(id == R.id.history){

        }
        return super.onOptionsItemSelected(item);
    }
    public Pair<Integer, String> extractNum(String expression){//tested
        String num = "";
        int idx = 0;
        for(int i=0; i< expression.length(); i++){
            if(Character.isDigit(expression.charAt(i))){
                num+=expression.charAt(i);
                idx = i;
            }
            else{
                break;
            }
        }
        if(!num.equals("")) {
            return Pair.create(Integer.valueOf(num), expression.substring(idx + 1));
        }
        else{
            return Pair.create(new Integer(1), expression);
        }
    }
    public long apply(char op, long x1, long x2){
        if(op=='+'){
            return x1+x2;
        }
        else if (op=='-'){
            return x1-x2;
        }
        else {
            return x1 * x2;
        }
    }
    public Pair<Long, String > evalGroup(String group){
        long coeff = 1;
        long num = 0;
        StringBuilder results = new StringBuilder("");
        while(group.length()>0){
            if(Character.isDigit(group.charAt(0))){
                Pair<Integer, String> pair = extractNum(group);
                coeff=pair.first;
                group = pair.second;
            }
            else if(group.charAt(0)=='d'){
                group = group.substring(1);
                Pair<Integer, String> pair = extractNum(group);
                Random generator = new Random();
                for(int i=0; i<coeff; i++) {
                    Integer rando = generator.nextInt(pair.first)+1;
                    results.append( rando.toString()+", ");
                    num += rando;
                }
                group = pair.second;
            }
        }
        return Pair.create(num,results.toString());
    }
    public Pair<Long, String> evaluate(String expression) {//tested
        String orig = expression;
        long sum = 0;
        ArrayList<Long> quants = new ArrayList<Long>();
        ArrayList<Character> operators = new ArrayList<Character>();
        operators.add('+');
        StringBuilder group = new StringBuilder("");
        StringBuilder tag = new StringBuilder("");
        while(expression.length() > 0){
            if(Character.isDigit(expression.charAt(0))||expression.charAt(0)=='d'){
                group.append(expression.charAt(0));
                expression = expression.substring(1);
            }
            else if (expression.charAt(0)=='+'||expression.charAt(0)=='-'){
                if(group.length()>0) {
                    Pair<Long, String> grp = evalGroup(group.toString());
                    quants.add(grp.first);
                    tag.append(grp.second);
                }
                group.delete(0,group.length());
                operators.add(expression.charAt(0));
                expression = expression.substring(1);
            }
        }
        if(group.length()>0){
            Pair<Long, String> grp = evalGroup(group.toString());
            quants.add(grp.first);
            tag.append(grp.second);
        }

        if(quants.size()!=operators.size()){
            Toast.makeText(this, "Expression invalid, please clear and try again",Toast.LENGTH_LONG).show();
            return Pair.create(new Long(0),"");
        }
        for(int i=0; i<quants.size(); i++){
            sum = apply(operators.get(i),sum, quants.get(i));
        }
        return Pair.create(sum,tag.toString());
    }
}
