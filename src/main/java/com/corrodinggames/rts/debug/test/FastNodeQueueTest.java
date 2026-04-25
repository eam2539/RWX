package com.corrodinggames.rts.debug.test;

import com.corrodinggames.rts.gameFramework.GameEngine;
import com.corrodinggames.rts.gameFramework.pathfinding.FastNodeQueue;
import com.corrodinggames.rts.gameFramework.pathfinding.FastNodeQueue2;
import com.corrodinggames.rts.gameFramework.pathfinding.NodeQueue;
import com.corrodinggames.rts.gameFramework.pathfinding.PathOpenListNode;

/* JADX INFO: renamed from: com.corrodinggames.rts.a.a.k */
/* JADX INFO: loaded from: game-lib.jar:com/corrodinggames/rts/a/a/k.class */
public class FastNodeQueueTest extends Test {
    /* JADX INFO: renamed from: a */
    public PathOpenListNode createTestNode(int i) {
        PathOpenListNode pathOpenListNode = new PathOpenListNode();
        pathOpenListNode.a((short) i, (short) 0);
        pathOpenListNode.a(0, 0, 0);
        return pathOpenListNode;
    }

    /* JADX INFO: renamed from: a */
    public void runAllTests() {
        GameEngine.isInSpace("== Testing FastNodeQueue ==");
        runQueueTest(new FastNodeQueue());
        GameEngine.isInSpace("== Testing FastNodeQueue2 ==");
        runQueueTest(new FastNodeQueue2());
    }

    /* JADX INFO: renamed from: a */
    public void runQueueTest(NodeQueue nodeQueue) {
        PathOpenListNode pathOpenListNodeCreateTestNode = createTestNode(1);
        PathOpenListNode pathOpenListNodeCreateTestNode2 = createTestNode(2);
        PathOpenListNode pathOpenListNodeCreateTestNode3 = createTestNode(3);
        PathOpenListNode pathOpenListNodeCreateTestNode4 = createTestNode(4);
        GameEngine.isInSpace("sequential");
        nodeQueue.b();
        nodeQueue.a(pathOpenListNodeCreateTestNode);
        nodeQueue.a(pathOpenListNodeCreateTestNode2);
        nodeQueue.a(pathOpenListNodeCreateTestNode3);
        nodeQueue.a(pathOpenListNodeCreateTestNode4);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode2);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode3);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode4);
        GameEngine.isInSpace("reverse sequence");
        nodeQueue.b();
        nodeQueue.a(pathOpenListNodeCreateTestNode4);
        nodeQueue.a(pathOpenListNodeCreateTestNode3);
        nodeQueue.a(pathOpenListNodeCreateTestNode2);
        nodeQueue.a(pathOpenListNodeCreateTestNode);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode2);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode3);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode4);
        GameEngine.isInSpace("sequential with noise");
        nodeQueue.b();
        nodeQueue.a(pathOpenListNodeCreateTestNode);
        for (int i = 0; i < 1000; i++) {
            nodeQueue.a(createTestNode(100 + i));
        }
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode);
        nodeQueue.a(pathOpenListNodeCreateTestNode2);
        nodeQueue.a(pathOpenListNodeCreateTestNode3);
        for (int i2 = 0; i2 < 1000; i2++) {
            nodeQueue.a(createTestNode(100 + i2));
        }
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode2);
        nodeQueue.a(pathOpenListNodeCreateTestNode4);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode3);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode4);
        GameEngine.isInSpace("reverse sequence with noise");
        nodeQueue.b();
        nodeQueue.a(pathOpenListNodeCreateTestNode4);
        nodeQueue.a(pathOpenListNodeCreateTestNode3);
        for (int i3 = 0; i3 < 1000; i3++) {
            nodeQueue.a(createTestNode(100 + i3));
        }
        nodeQueue.a(pathOpenListNodeCreateTestNode2);
        for (int i4 = 0; i4 < 1000; i4++) {
            nodeQueue.a(createTestNode(100 + i4));
        }
        nodeQueue.a(pathOpenListNodeCreateTestNode);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode2);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode3);
        Assert.assertEquals(nodeQueue.a(), pathOpenListNodeCreateTestNode4);
    }
}
