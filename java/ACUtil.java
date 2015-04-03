package com.sankuai.meituan.deal.util;

import com.sankuai.meituan.deal.domain.DealCategory;
import com.sankuai.meituan.deal.domain.Keyword;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

/**
 * AC多模匹配算法
 * User: yuzhen
 * Date: 14-4-21
 * Time: 上午11:33
 */
@Component
public class ACUtil {

    /**
     * 树的节点
     */
    static class Node{
        int state;               //自动机的状态，也就是节点数字
        char character = 0;      //指向当前节点的字符
        Node failureNode = this; //匹配失败时，下一个节点
        List<Keyword> keywords;       //匹配成功时，当前节点对应的关键词
        List<Node> children;         //当前节点的子节点

        public Node() {
        }

        public Node(int state, char character, Node failureNode) {
            this.state = state;
            this.character = character;
            this.failureNode = failureNode;
        }

        public boolean containsChild(char c){
            if(children != null){
                for(Node node : children){
                    if(node.character == c){
                        return true;
                    }
                }
            }
            return false;
        }

        public Node getChild(char c){
            if(children != null){
                for(Node node : children){
                    if(node.character == c){
                        return node;
                    }
                }
            }
            return null;
        }

        public void addChild(Node node) {
            if(children == null) {
                children = new ArrayList<Node>();
            }
            children.add(node);
        }

        public void addKeyword(Keyword keyword){
            if(keywords == null){
                keywords = new ArrayList<Keyword>();
            }
            keywords.add(keyword);
        }

        public void addAllKeyword(List<Keyword> keywords) {
            if(keywords == null){
                keywords = new ArrayList<Keyword>();
            }
            keywords.addAll(keywords);
        }
    }

    /**
     * 待匹配的模式串
     */
    public static class Patterns{

        protected final Node root = new Node();
        protected List<Node> tree;

        public Patterns(List<Keyword> keywords) {
            tree = new ArrayList<Node>();
            tree.add(root);
            for(Keyword kw : keywords){
                addKeyword(kw);
            }
            setFailNode();
        }

        private void addKeyword(Keyword keyword) {
            char[] wordCharArr = keyword.getWord().toCharArray();
            Node current = root;
            for(char currentChar : wordCharArr){
                if(current.containsChild(currentChar)){
                    current = current.getChild(currentChar);
                } else {
                    Node node = new Node(tree.size(), currentChar, root);
                    current.addChild(node);
                    current = node;
                    tree.add(node);
                }
            }
            current.addKeyword(keyword);
        }

        private void setFailNode(){
            Queue<Node> queue = new LinkedList<Node>();
            Node node = root;
            for (Node d1 : node.children)
                queue.offer(d1);

            while (!queue.isEmpty()) {
                node = queue.poll();
                if (node.children != null) {
                    for (Node curNode : node.children) {
                        queue.offer(curNode);
                        Node failNode = node.failureNode;
                        while (!failNode.containsChild(curNode.character)) {
                            failNode = failNode.failureNode;
                            if (failNode.state == 0) break;
                        }
                        if (failNode.containsChild(curNode.character)) {
                            curNode.failureNode = failNode.getChild(curNode.character);
                            curNode.addAllKeyword(curNode.failureNode.keywords);
                        }
                    }
                }
            }
        }

        public final Node getRoot() {
            return root;
        }

    }

    private Patterns patterns;

    public void createKeywordTree(List<Keyword> keywords) {
        this.patterns = new Patterns(keywords);
    }

    public Set<Keyword> searchKeyword(String data, DealCategory category) {
        return searchKeyword(data, category, true);
    }

    public Set<Keyword> searchKeyword(String data, DealCategory category, boolean recursive) {
        Set<Keyword> matchResult = new HashSet<Keyword>();

        Node node = patterns.getRoot();
        char[] chs = data.toCharArray();
        for(int i=0; i < chs.length; i++){
            while (!node.containsChild(chs[i])) {
                node = node.failureNode;
                if (node.state == 0) break;
            }
            if(node.containsChild(chs[i])){
                node = node.getChild(chs[i]);
                if(node.keywords != null){
                    for(Keyword pattern : node.keywords){
                        if (category == null) {
                            matchResult.add(pattern);
                        } else {
                            if (shouldContain(pattern, category, recursive)) {
                                matchResult.add(pattern);
                            }
                        }
                    }
                }
            }
        }

        return matchResult;
    }

    private boolean shouldContain(Keyword keyword, DealCategory category, boolean recursive) {
        if (!recursive) {
            return keyword.getCategories().contains(category.getId());
        }

        //顶级品类直接比较，非顶级品类往上匹配
        if (category.getLevel() == 1) {
            return keyword.getCategories().contains(category.getId());
        } else {
            DealCategory tmp = category;
            while (tmp != null) {
                if (keyword.getCategories().contains(tmp.getId())) {
                    return true;
                } else {
                    tmp = tmp.getParent();
                }
            }
        }
        return false;
    }

}
