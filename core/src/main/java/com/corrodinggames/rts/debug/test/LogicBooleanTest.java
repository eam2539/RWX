package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.game.PlayerTeam;
import com.corrodinggames.rts.game.units.BaseUnit;
import com.corrodinggames.rts.game.units.OrderableUnit;
import com.corrodinggames.rts.game.units.custom.AnimationTag;
import com.corrodinggames.rts.game.units.custom.CustomUnit;
import com.corrodinggames.rts.game.units.custom.CustomUnitConfig;
import com.corrodinggames.rts.game.units.custom.logicBooleans.BooleanParseException;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBoolean;
import com.corrodinggames.rts.game.units.custom.logicBooleans.LogicBooleanLoader;
import com.corrodinggames.rts.game.units.custom.logicBooleans.VariableScope;
import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.PerformanceProfiler;
import com.corrodinggames.rts.gameFramework.Utility;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.c */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/c.class */
public class LogicBooleanTest extends Test {
    /* JADX INFO: renamed from: a */
    public void runTests() {
        GameEngine.log("Logic boolean tests");
        CustomUnitConfig customUnitConfig = CustomUnitConfig.instance;
        defineMemory(customUnitConfig, "number numA");
        defineMemory(customUnitConfig, "number numB");
        defineMemory(customUnitConfig, "number[] numArrayA");
        defineMemory(customUnitConfig, "number[] numArrayB");
        defineMemory(customUnitConfig, "bool[] boolArrayA");
        defineMemory(customUnitConfig, "unit[] unitArrayA");
        BaseUnit baseUnitA = CustomUnitConfig.a(false, customUnitConfig);
        OrderableUnit orderableUnitA = CustomUnitConfig.a(false, customUnitConfig);
        orderableUnitA.setUnitTeam(PlayerTeam.TEAM_ALL);
        OrderableUnit orderableUnitA2 = CustomUnitConfig.a(false, customUnitConfig);
        orderableUnitA2.setUnitTeam(PlayerTeam.TEAM_ALL);
        orderableUnitA2.currentHealth = 44.0f;
        setMemory(orderableUnitA2, "numA=5");
        setMemory(orderableUnitA2, "numB=7");
        setMemory(orderableUnitA2, "numArrayA[0]=1");
        setMemory(orderableUnitA2, "numArrayA[1]=2");
        setMemory(orderableUnitA2, "numArrayA[2]=15");
        setMemory(orderableUnitA2, "boolArrayA[0]=true");
        setMemory(orderableUnitA2, "unitArrayA[0]=self");
        setMemory(orderableUnitA2, "numArrayA[(5)]=5");
        setMemory(orderableUnitA2, "numArrayA[5+5]=10");
        setMemory(orderableUnitA2, "numArrayA[4+4]=8");
        setMemory(orderableUnitA2, "boolArrayA[10]=true");
        setMemory(orderableUnitA2, "unitArrayA[10]=self");
        GameEngine.log("string: " + getLogicBooleanValue(orderableUnitA2, parseLogicBoolean("str(memory.numArrayA)")));
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(5)"), 5.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(10)"), 10.0f);
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.boolArrayA[10]"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.unitArrayA[10]==self"));
        setMemory(orderableUnitA2, "numArrayA[memory.numArrayA.get(2)]=98");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(15)"), 98.0f);
        setMemory(orderableUnitA2, "numArrayA[memory.numArrayA[2]]=99");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(15)"), 99.0f);
        setMemory(orderableUnitA2, "numArrayA[((((((((6))))))))]=99");
        setMemory(orderableUnitA2, "numArrayA[((((((((memory.numArrayA[2]))))))))]=88");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(15)"), 88.0f);
        setMemoryWithLogicBoolean(orderableUnitA2, "numArrayA[((((((((memory.numArrayA[2])())))))]=77");
        setMemoryWithLogicBoolean(orderableUnitA2, "numArrayA[((((((((memory.numArrayA[2])))[)))]]))]=66");
        setMemoryWithLogicBoolean(orderableUnitA2, "numArrayA[a]=1");
        setMemoryWithLogicBoolean(orderableUnitA2, "numArrayA[0]='a'");
        setMemory(orderableUnitA2, "numArrayA[9001]=5");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.size"), 9002.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.length"), 9002.0f);
        setMemory(orderableUnitA2, "numArrayA[11000]=5");
        setMemory(orderableUnitA2, "numArrayA[10000]=5");
        setMemory(orderableUnitA2, "numArrayA[10001]=6");
        setMemory(orderableUnitA2, "numArrayA[9999]=42");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(11000)"), 0.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(10000)"), 5.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(10001)"), 0.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(9999)"), 42.0f);
        setMemory(orderableUnitA2, "numArrayA[21]=21");
        setMemory(orderableUnitA2, "numArrayA[22]=memory.numArrayA[21]");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(22)"), 21.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(0)"), 1.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(1)"), 2.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(5)"), 5.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(500)"), 0.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA.get(9000)"), 0.0f);
        assertInvalid("memory.numArrayA.get('A')");
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA[0]"), 1.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA[1]"), 2.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA[0]+memory.numArrayA[1]"), 3.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA[0]+(memory.numArrayA[1])"), 3.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("(memory.numArrayA[0]+(memory.numArrayA[1]))"), 3.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.numArrayA[5]"), 5.0f);
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.numArrayA.contains(5)"));
        assertFalse(orderableUnitA2, parseLogicBoolean("memory.numArrayA.contains(777)"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.numArrayA.contains(memory.numArrayA[5])"));
        assertInvalid("memory.numArrayA.contains('a')");
        assertInvalid("memory.numArrayA.contains(true)");
        assertInvalid("memory.numArrayA[5][5]");
        assertInvalid("memory.numArrayA[5][5][60]");
        assertInvalid("memory.numArrayA[5][5][[60]]");
        assertInvalid("memory.numArrayA[5][[5]");
        assertInvalid("memory.numArrayA[5]][5]");
        assertInvalid("memory.numArrayA[5[]][5]");
        assertInvalid("memory.numArrayA[[5[]][5]");
        CustomUnit customUnitA = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA.posX = 10.0f;
        customUnitA.currentHealth = 55.0f;
        customUnitA.maxHealth = 500.0f;
        orderableUnitA2.unitTarget2 = customUnitA;
        setMemory(customUnitA, "numA=309");
        setMemory(customUnitA, "numB=409");
        CustomUnit customUnitA2 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA2.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA2.posY = 5.0f;
        customUnitA2.currentHealth = 66.0f;
        customUnitA2.maxHealth = 1000.0f;
        customUnitA.unitTarget3 = customUnitA2;
        CustomUnit customUnitA3 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA3.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA3.posX = 2.0f;
        setMemory(customUnitA3, "numA=99");
        setMemory(customUnitA3, "numB=88");
        CustomUnit customUnitA4 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA4.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA4.posX = 3.0f;
        setMemory(customUnitA4, "numA=239");
        setMemory(customUnitA4, "numB=268");
        CustomUnit customUnitA5 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA5.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA5.posX = 3.0f;
        customUnitA3.loadTransportedUnit(customUnitA4);
        customUnitA3.loadTransportedUnit(customUnitA5);
        CustomUnit customUnitA6 = CustomUnitConfig.a(false, customUnitConfig);
        customUnitA6.setUnitTeam(PlayerTeam.TEAM_ALL);
        customUnitA6.a(AnimationTag.a("globalTag1, globalTag2"), false);
        customUnitA6.posX = 2.0f;
        GameEngine.log("=== logic boolean tests == (runs:50)");
        Long lValueOf = Long.valueOf(PerformanceProfiler.a());
        for (int i = 0; i < 50; i++) {
            if (i == 1) {
            }
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("true"));
            assertFalse(orderableUnitA2, parseLogicBoolean("false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("not false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("not not true"));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("5+5"), 10.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("1+2+3"), 6.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("2.5+2.5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("10-2"), 8.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("((5+5)-2)*3"), 24.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("10/2+10*2"), 25.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-5"), -5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("--5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("9--5"), 14.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-9--5"), -4.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("+5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("+ 5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" + 5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" ++ 5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-+5"), -5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("--+5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("++-5"), -5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" - - +5"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("9++5"), 14.0f);
            assertInvalid("5 - ");
            assertInvalid("5 -- ");
            assertInvalid("5 + ");
            assertInvalid("5 ++ ");
            assertInvalid("5 ** 9 ");
            assertInvalid("5 -/ 9 ");
            assertInvalid("5 * 5 -");
            assertInvalid(" - ");
            assertInvalid(" -- ");
            assertInvalid(" + ");
            assertInvalid(" ++ ");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean(" 'hello'"), "hello");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean(" \"hello\" "), "hello");
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.hp+1"), orderableUnitA2.currentHealth + 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.x+1"), orderableUnitA2.posX + 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.y+1"), orderableUnitA2.posY + 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.z+1"), orderableUnitA2.posZ + 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("int( 5.5+0.1 )"), 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-5 * 5"), -25.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-5 * self.hp"), (-5.0f) * orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.hp + -5"), orderableUnitA2.currentHealth - 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.hp * -5"), (-5.0f) * orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("(self.hp ) * -5 "), (-5.0f) * orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-self.hp * -5"), (-5.0f) * (-orderableUnitA2.currentHealth));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-(self.hp ) * -5 "), (-5.0f) * (-orderableUnitA2.currentHealth));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-5 * -self.hp"), (-5.0f) * (-orderableUnitA2.currentHealth));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("(-5 * -self.hp)/2"), ((-5.0f) * (-orderableUnitA2.currentHealth)) / 2.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-(self.hp )"), -orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("--(self.hp )"), orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-((self.hp ))"), -orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-self.hp"), -orderableUnitA2.currentHealth);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-0"), 0.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-  1"), -1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" -  1"), -1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-0*-0"), 0.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-2*2"), -4.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("-2-3-2"), -7.0f);
            assertFalse(orderableUnitA2, parseLogicBoolean("10>10"));
            assertFalse(orderableUnitA2, parseLogicBoolean("10<10"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("10>=10"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("10<=10"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hello'=='hello'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hello'!='bye'"));
            assertInvalid("'hello'<'bye'");
            assertInvalid("'hello'>'bye'");
            assertInvalid("'hello'<='bye'");
            assertInvalid("'hello'>='bye'");
            assertInvalid("'hello'55'bye'");
            assertInvalid("'hello'><'bye'");
            assertInvalid("6><8");
            assertFalse(orderableUnitA2, parseLogicBoolean("not (10>5 and true)"));
            assertFalse(orderableUnitA2, parseLogicBoolean("not true and false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("not false and true"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("not (false and true)"));
            assertFalse(orderableUnitA2, parseLogicBoolean("not (1>2 or 5>2)"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("(true and (false or true))"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" true and   (false   or   true  )"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("true and((false)or(true) )"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("100>50+20"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("100>50*1.5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("not (100<50*1.5)"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("5 < 10 < 15"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("false==false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("true==true"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("false==false==false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("true==true==true"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("false!=true!=false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("true!=false!=true"));
            assertFalse(orderableUnitA2, parseLogicBoolean("'test'==null"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'test'!=null"));
            assertFalse(orderableUnitA2, parseLogicBoolean("'test'==null==null"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'test'!=null!='test2'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self!=null"));
            assertFalse(orderableUnitA2, parseLogicBoolean("self==null"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("10==10"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("10.5==10.5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("1/3==1/3"));
            assertFalse(orderableUnitA2, parseLogicBoolean("10!=10"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("10!=5"));
            assertInvalid("true - true");
            assertInvalid("true + true");
            assertInvalid("true * true");
            assertInvalid("true / true");
            assertInvalid("true < 10");
            assertInvalid("true == 10");
            assertInvalid("true != 10");
            assertInvalid("'text' == 10");
            assertInvalid("10 == ");
            assertInvalid("10 != ");
            assertInvalid("10 > ");
            assertInvalid("10 < ");
            assertInvalid("10 >= ");
            assertInvalid("10 <= ");
            assertInvalid("10 ==");
            assertInvalid("10 !=");
            assertInvalid("10 >");
            assertInvalid("10 <");
            assertInvalid("10 >=");
            assertInvalid("10 <=");
            assertInvalid("==10");
            assertInvalid("!=10");
            assertInvalid(">10");
            assertInvalid("<10");
            assertInvalid(">=10");
            assertInvalid("<=10");
            assertInvalid("10.6.6");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("select(true, 'A','B')"), "A");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("select(false, 'A','B')"), "B");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("str(5.5)"), "5.5");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("str(5)"), "5");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("lowercase('HELlo')"), "hello");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("uppercase('heLLo')"), "HELLO");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("lowercase(str('HELlo'))"), "hello");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'hello'"), "hello");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'hello'+' world'"), "hello world");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'he(llo'+' world'"), "he(llo world");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'he(llo'+' wor)ld'"), "he(llo wor)ld");
            assertInvalid("('hello'+' world'");
            assertInvalid("'hello'+)' world'");
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.hp(lessThan=9999)"));
            assertInvalid("self.hp(lessThan=9999, lessThan=9998)");
            assertInvalid("self..hp(lessThan=9999)");
            assertInvalid("self...hp(lessThan=9999)");
            parseLogicBoolean("game.nukesEnabled()");
            assertInvalid("game.nukesEnabled(nukesEnabled=true)");
            assertInvalid("game.nukesEnabled(nukesEnabled=false)");
            assertInvalid("game.nukesEnabled()==0");
            assertInvalid("game.nukesEnabled()!=0");
            assertInvalid("game.nukesEnabled()<0");
            assertInvalid("game.nukesEnabled()>0");
            assertInvalid("game.nukesEnabled()=='true'");
            assertInvalid("game.nukesEnabled()!='true'");
            assertInvalid("self.nukesEnabled()");
            assertInvalid("parent.nukesEnabled()");
            assertInvalid("hp==44");
            assertInvalid("5=44");
            if (orderableUnitA2 == PlayerTeam.TEAM_ALL.teamCommandCenter) {
                GameEngine.log("skipping for placeholderTeamUnit");
            } else {
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.hp==44"));
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.customTarget1.hp==55"));
                assertInvalid("self.memory1.hp=55");
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.customTarget1.maxhp==500"));
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("customTarget1.hp==55"));
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.customTarget1.customTarget2.hp==66"));
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.customTarget1==self.customTarget1"));
                parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.customTarget1!=self"));
                parseLogicBooleanWithUnknown(orderableUnitA, parseLogicBoolean("self.customTarget1==null"));
                parseLogicBooleanWithUnknown(orderableUnitA, parseLogicBoolean("self.customTarget1!=self"));
                parseLogicBooleanWithUnknown(orderableUnitA, parseLogicBoolean("self.parent==null"));
                parseLogicBooleanWithUnknown(orderableUnitA, parseLogicBoolean("self.parent.customTarget1==null"));
                parseLogicBooleanWithUnknown(orderableUnitA, parseLogicBoolean("self.parent.customTarget1==self.parent"));
                parseLogicBooleanWithUnknown(orderableUnitA, parseLogicBoolean("self.parent.customTarget1!=self"));
            }
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.getOffsetAbsolute(y=10).y"), orderableUnitA2.posY + 10.0f);
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.getOffsetAbsolute(y=10).y==self.y+10"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("self.getOffsetRelative(y=10, height=5).height==self.height+5"));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.getOffsetRelative(y=10, height=5).height"), orderableUnitA2.posZ + 5.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.getOffsetAbsolute(y=10).getOffsetAbsolute(y=10).y"), orderableUnitA2.posY + 10.0f + 10.0f);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().getOffsetAbsolute(x=5).x"), customUnitA4.posX + 5.0f);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting(slot=1).getOffsetAbsolute(x=5).x"), customUnitA5.posX + 5.0f);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().parent.transporting().parent.id"), customUnitA3.objectId);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().getOffsetAbsolute(x=memory.numA).x-memory.numA"), customUnitA4.posX);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().getOffsetAbsolute(x=self.id).x-self.id"), customUnitA4.posX);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().parent.transporting().getOffsetAbsolute(x=self.id).x-self.id"), customUnitA4.posX);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().parent.transporting().getOffsetAbsolute(x=self.id).getOffsetAbsolute().x-self.id"), customUnitA4.posX);
            assertLogicBooleanText(customUnitA3, parseLogicBoolean("self.transporting().parent.transporting().getOffsetAbsolute(x=self.id).getOffsetAbsolute(x=self.id+1).x"), customUnitA4.posX + customUnitA3.objectId + customUnitA3.objectId + 1.0f);
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("numberOfUnitsInTeam(greaterThan=-2)"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("NumberOfUnitsInTeam(greaterTHAN=-2)"));
            assertLogicBooleanValue(orderableUnitA2, parseLogicBoolean("self.noUnitInTeam()"));
            assertLogicBooleanValue(orderableUnitA2, parseLogicBoolean("self.hasUnitInTeam()"));
            assertLogicBooleanValue(orderableUnitA2, parseLogicBoolean("self.hasUnitInTeam(neutralTeam=true)"));
            assertLogicBooleanValue(orderableUnitA2, parseLogicBoolean("self.shield()+self.ammo()+self.hp()>-1"));
            assertLogicBooleanValue(orderableUnitA2, parseLogicBoolean("parent.shield()+parent.ammo()+parent.hp()>-1"));
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'hello'+'a'"), "helloa");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'hello'+5"), "hello5");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("substring('hello',0,3)"), "hel");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("substring('hello',0,100)"), "hello");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("substring('HEllo',0,100)"), "HEllo");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("'HEllo'"), "HEllo");
            assertLogicBooleanUnit(orderableUnitA2, parseLogicBoolean("substring('aa',0,2)+substring('bb',0,2)"), "aabb");
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" true AND true"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" true aNd true"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" true OR false"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" true OR TRUE"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" True OR  False "));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" True OR  (False) "));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" NOT FALSE "));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" NOT NOT NOT FALSE "));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" game.nukesEnabled "));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" GAME.NukesEnabled "));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("squareRoot( 100 )"), 10.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max(+1,2)"), 2.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min(+1,2)"), 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max(1,2)"), 2.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min(1,2)"), 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max( 1,2 )"), 2.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min( 1,2 )"), 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max(-1,2)"), 2.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min(-1,2)"), -1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max( -1,2 )"), 2.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min( -1,2 )"), -1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max( 3,1 )"), 3.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min( 3,1 )"), 1.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max( 3+3,4 )"), 6.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min( 3+3,4 )"), 4.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("distanceSquared( 1,1,1,6 )"), 25.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("max(distanceSquared( 1,1,1,6 ), 4)"), 25.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("min(  distanceSquared( 1,1,1 , 6 )  , 4)"), 4.0f);
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("distanceSquared( 1,1,1,6 )>=5*5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("distanceSquared( 1,1,1,6 )>4*5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("distanceSquared( 1,1,6,1 )<6*5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("distance( 0,0,5,0 )==5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("distance( 0,1,0,6 )==5"));
            assertLogicBooleanHolder(orderableUnitA2, parseLogicBoolean("customTarget1"), customUnitA);
            assertLogicBooleanHolder(orderableUnitA2, parseLogicBoolean("customTarget1.self"), customUnitA);
            assertLogicBooleanHolder(orderableUnitA2, parseLogicBoolean("self.customTarget1.self"), customUnitA);
            assertLogicBooleanHolder(orderableUnitA2, parseLogicBoolean("customTarget1.customTarget2"), customUnitA.unitTarget3);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" distanceBetween(customTarget1.customTarget2, customTarget1 ) "), Utility.distance(customUnitA.posX, customUnitA.posY, customUnitA2.posX, customUnitA2.posY));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" distanceBetweenSquared(customTarget1.customTarget2, customTarget1 ) "), Utility.distanceSq(customUnitA.posX, customUnitA.posY, customUnitA2.posX, customUnitA2.posY));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean(" distanceBetween(self, nullUnit ) == 0 "));
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" distanceBetween(customTarget1.customTarget2, nullUnit ) "), 0.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" distanceBetween(nullUnit, nullUnit ) "), 0.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" distanceBetween( self.getOffsetAbsolute(x=5), self.getOffsetAbsolute(x=-5) ) "), 10.0f);
            assertLogicBooleanText(orderableUnitA2, parseLogicBoolean(" distanceBetweenSquared( self.getOffsetAbsolute(x=5), self.getOffsetAbsolute(x=-5) ) "), 100.0f);
            parseLogicBooleanWithUnknown(orderableUnitA2, assertTrue(" self.energy < 0.5 and customTarget2==nullUnit ", true));
            parseLogicBooleanWithUnknown(orderableUnitA2, assertTrue(" self.energy < 0.5 and customTarget2 == nullUnit ", true));
            parseLogicBooleanWithUnknown(orderableUnitA2, assertTrue(" self.energy < 0.5 and customTarget1 != nullUnit ", true));
            parseLogicBooleanWithUnknown(orderableUnitA2, assertTrue("parent==nullUnit and customTarget1!= nullUnit ", true));
            parseLogicBooleanWithUnknown(orderableUnitA2, assertTrue("parent == nullUnit and customTarget1!=nullUnit ", true));
            assertInvalid("distanceBetween( self )");
            assertInvalid("distanceBetween( self, 5 )");
            assertInvalid("distanceBetween( self, nullUnit, nullUnit )");
            assertInvalid("distanceBetween(  )");
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'and'=='and'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'and'!='and true'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hello.test'!='bye'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hel.lo.test'!='b.ye'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hel.lo.(test'!='b.ye'"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hel.\"lo.(test'!='b.ye \"and '"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("\"hel.lo.'(test2\"!='b.ye \"and '"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("5==5"));
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("'hel.lo.(test'!='b.ye' and 5==5"));
            assertInvalid("distanceSquared(  )");
            assertInvalid("distanceSquared( 1 )");
            assertInvalid("distanceSquared( 1,1 )");
            assertInvalid("distanceSquared( 1,1,1 )");
            assertInvalid("distanceSquared( 1,1,1,'test' )");
            assertInvalid("distanceSquared( 1,1,1,true )");
            assertInvalid("distanceSquared( 1,1,1,null )");
            assertInvalid("distanceSquared( 1,1,1, )");
            assertInvalid("distanceSquared( 1,1,1,'test' )");
            assertInvalid("distanceSquared( x1=1,1,1,'test' )");
            assertInvalid("distanceSquared( 1,1,1,1, x1=1 )");
            assertInvalid("distanceSquared( 1,1,1,1,1 )");
            assertInvalid("distanceSquared( 1,1,1, x1=1 )");
            assertInvalid("distanceSquared( 1,1,1, 1 ");
            assertInvalid("distanceSquared( 1,1,1, 1 ))");
            parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("SELF.HP(lessThan=9999)"));
            assertFalse(orderableUnitA2, parseLogicBoolean("Self.Parent.HP(lessThan=9999)"));
            assertInvalid("self.hasFlag( id=35 )");
            assertInvalid("self.hasFlag( 35 )");
            assertFalse(orderableUnitA2, parseLogicBoolean("self.hasFlag(id=30)"));
            assertFalse(orderableUnitA2, parseLogicBoolean("self.hasFlag(30)"));
            assertFalse(orderableUnitA2, parseLogicBoolean("self.hasFlag(15+15)"));
            assertFalse(orderableUnitA2, parseLogicBoolean("self.hasFlag(id=15*2)"));
        }
        GameEngine.log("Took: " + PerformanceProfiler.a(lValueOf.longValue(), Long.valueOf(PerformanceProfiler.a()).longValue()));
        GameEngine.log("=== logic boolean memory tests ==");
        defineMemory(customUnitConfig, "unit testUnit1, float testFloat1");
        defineMemory(customUnitConfig, "unit testUnit2, float testFloat2");
        defineMemory(customUnitConfig, "bool testBool1");
        defineMemory(customUnitConfig, "number testNumber1");
        defineMemory(customUnitConfig, "float  testNumber2");
        defineMemory(customUnitConfig, "String testString");
        defineMemory(customUnitConfig, "String nullString");
        setMemory(orderableUnitA2, "testString='(,,((', testFloat1=5, testFloat2=8, testBool1=true, testUnit1=self");
        setMemory(orderableUnitA2, "nullString=null");
        setMemoryWithLogicBoolean(baseUnitA, "testNumber1=null");
        setMemoryWithLogicBoolean(baseUnitA, "testNumber2=null");
        setMemoryWithLogicBoolean(baseUnitA, "testBool1=null");
        setMemoryWithLogicBoolean(baseUnitA, "testNumber1=self");
        setMemoryWithLogicBoolean(baseUnitA, "testBool1=5");
        GameEngine.log(orderableUnitA2.unitVariables.debugMemory(false, true));
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.testFloat1"), 5.0f);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("memory.testFloat2"), 8.0f);
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testFloat1==5"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testString=='(,,(('"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testBool1"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testBool1==true"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testUnit1==self"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testUnit1!=nullUnit"));
        defineMemoryWithUnknown("memory.testUnit1==5", true);
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("self.readUnitMemory('testFloat1', type='float')"), 5.0f);
        setMemory(orderableUnitA2, "testFloat1=distance( 0,0,6,0 ), testFloat2=16");
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testFloat1==6"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("memory.testFloat2==16"));
        setMemory(orderableUnitA2, "testUnit1=self.getOffsetAbsolute(y=10), testUnit2=self.getOffsetAbsolute(y=-10)");
        GameEngine.log(orderableUnitA2.unitVariables.debugMemory(false, true));
        assertLogicBooleanText(orderableUnitA2, parseLogicBoolean("distanceBetween( memory.testUnit1, memory.testUnit2)"), 20.0f);
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("distanceBetweenSquared( memory.testUnit1, memory.testUnit2)==20*20"));
        assertLogicBooleanHolder(orderableUnitA2, parseLogicBoolean("globalSearchForFirstUnit(withTag='globalTag1')"), customUnitA6);
        assertLogicBooleanHolder(orderableUnitA2, parseLogicBoolean("globalSearchForFirstUnit(withTag='globalTag2')"), customUnitA6);
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("globalSearchForFirstUnit()!=null"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("globalSearchForFirstUnit(withTag='globalTag1', relation='enemy')==null"));
        parseLogicBooleanWithUnknown(orderableUnitA2, parseLogicBoolean("globalSearchForFirstUnit(withTag='globalTagNo')==null"));
        defineMemoryWithUnknown("globalSearchForFirstUnit(withTag='globalTag1', relation='XYZ')", true);
    }

    /* JADX INFO: renamed from: a */
    public void assertInvalid(String str) {
        defineMemoryWithUnknown(str, false);
    }

    /* JADX INFO: renamed from: a */
    public void defineMemoryWithUnknown(String str, boolean z) {
        try {
            LogicBooleanLoader.parseBooleanBlock(CustomUnitConfig.instance, str, false);
            throw new RuntimeException("assertCreateError got no error for: " + str);
        } catch (RuntimeException e) {
            if (e.getClass() != RuntimeException.class && e.getClass() != BooleanParseException.class) {
                throw new RuntimeException(e);
            }
            if (z) {
                GameEngine.logDebug("assertCreateError: " + str + " error:" + e.getMessage());
            }
        }
    }

    /* JADX INFO: renamed from: a */
    public void defineMemory(CustomUnitConfig customUnitConfig, String str) {
        customUnitConfig.variableMapping.defineVariables(customUnitConfig, str);
    }

    /* JADX INFO: renamed from: a */
    public void setMemory(BaseUnit baseUnit, String str) {
        CustomUnit customUnit = (CustomUnit) baseUnit;
        VariableScope.createMemoryWriter(str, customUnit.unitConfig, "testsection", "testkey").writeToUnit(customUnit);
    }

    /* JADX INFO: renamed from: b */
    public void setMemoryWithLogicBoolean(BaseUnit baseUnit, String str) {
        try {
            CustomUnit customUnit = (CustomUnit) baseUnit;
            VariableScope.createMemoryWriter(str, customUnit.unitConfig, "testsection", "testkey").writeToUnit(customUnit);
            throw new RuntimeException("assertSetMemoryError got no error for: " + str);
        } catch (RuntimeException e2) {
        }
    }

    /* JADX INFO: renamed from: b */
    public LogicBoolean parseLogicBoolean(String str) {
        return assertTrue(str, false);
    }

    /* JADX INFO: renamed from: b */
    public LogicBoolean assertTrue(String str, boolean z) {
        try {
            return LogicBooleanLoader.parseBooleanBlock(CustomUnitConfig.instance, str, z);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error: " + e.getMessage() + " parsing [" + str + "]", e);
        }
    }

    /* JADX INFO: renamed from: a */
    public void assertLogicBooleanValue(OrderableUnit orderableUnit, LogicBoolean logicBoolean) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.bool) {
            throw new RuntimeException("Asset assertBooleanTrue type ==" + logicBoolean.getReturnType());
        }
        logicBoolean.read(orderableUnit);
    }

    /* JADX INFO: renamed from: b */
    public void parseLogicBooleanWithUnknown(OrderableUnit orderableUnit, LogicBoolean logicBoolean) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.bool) {
            throw new RuntimeException("Asset assertBooleanTrue type ==" + logicBoolean.getReturnType());
        }
        if (!logicBoolean.read(orderableUnit)) {
            throw new RuntimeException("Asset assertBooleanTrue failed, got false for: " + logicBoolean.getMatchFailReasonForPlayer(orderableUnit));
        }
    }

    /* JADX INFO: renamed from: c */
    public void assertFalse(OrderableUnit orderableUnit, LogicBoolean logicBoolean) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.bool) {
            throw new RuntimeException("Asset assertBooleanFalse type ==" + logicBoolean.getReturnType());
        }
        Assert.assertFalse(logicBoolean.read(orderableUnit));
    }

    /* JADX INFO: renamed from: a */
    public void assertLogicBooleanText(OrderableUnit orderableUnit, LogicBoolean logicBoolean, float f) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.number) {
            throw new RuntimeException("Asset assertBooleanNumber type ==" + logicBoolean.getReturnType());
        }
        float number = logicBoolean.readNumber(orderableUnit);
        if (Utility.abs(number - f) > 0.001f) {
            throw new RuntimeException("Asset failed (float):" + number + "!=" + f + " for: " + logicBoolean.getMatchFailReasonForPlayer(orderableUnit));
        }
    }

    /* JADX INFO: renamed from: d */
    public String getLogicBooleanValue(OrderableUnit orderableUnit, LogicBoolean logicBoolean) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.string) {
            throw new RuntimeException("Asset assertBooleanString type ==" + logicBoolean.getReturnType());
        }
        return logicBoolean.readString(orderableUnit);
    }

    /* JADX INFO: renamed from: a */
    public void assertLogicBooleanUnit(OrderableUnit orderableUnit, LogicBoolean logicBoolean, String str) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.string) {
            throw new RuntimeException("Asset assertBooleanString type ==" + logicBoolean.getReturnType());
        }
        Assert.assertEquals(logicBoolean.readString(orderableUnit), str);
    }

    /* JADX INFO: renamed from: a */
    public void assertLogicBooleanHolder(OrderableUnit orderableUnit, LogicBoolean logicBoolean, BaseUnit baseUnit) {
        if (logicBoolean.getReturnType() != LogicBoolean.ReturnType.unit) {
            throw new RuntimeException("Asset assertBooleanUnit type ==" + logicBoolean.getReturnType());
        }
        BaseUnit unit = logicBoolean.readUnit(orderableUnit);
        if (unit != baseUnit) {
            GameEngine.log("class: " + logicBoolean.getClass().getName());
            throw new RuntimeException("assertBooleanUnit failed:" + BaseUnit.serialize(unit) + "!=" + BaseUnit.serialize(baseUnit) + " for: " + logicBoolean.getMatchFailReasonForPlayer(orderableUnit));
        }
    }
}
