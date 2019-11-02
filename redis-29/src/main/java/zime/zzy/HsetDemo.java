package zime.zzy;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class HsetDemo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        Post post = new Post();
        post.setTitle("bt");
        post.setAuthor("zzy");
        post.setContent("content");
        Long postId = savePost(jedis, post);
        Map<String, String> myBlog = getPost(jedis, postId);

        for (String key:myBlog.keySet()){
            System.out.println(key+":"+myBlog.get(key));
        }
    }

    static Map<String, String> getPost(Jedis jedis, Long postId) {
        Map<String, String> myBlog = jedis.hgetAll("post:" + postId);
        return myBlog;
    }

    static Long savePost(Jedis jedis,Post post){
        Long postId = jedis.incr("posts");
        Map<String,String> myPost = new HashMap<String,String>();
        myPost.put("title",post.getTitle());
        myPost.put("author",post.getAuthor());
        myPost.put("content",post.getContent());
        jedis.hmset("post:"+postId,myPost);
        return postId;
    }

    @Test
    public void test2(){
        Jedis jedis = new Jedis("localhost");
        User user= new User();
        user.setName("zzy");
        user.setSex("male");
        String userStr = JSON.toJSONString(user);
        jedis.set("user",userStr);
        String user1 = jedis.get("user");
        System.out.println(user1);
        User user2 = JSON.parseObject(user1, User.class);
        System.out.println(user2);
        jedis.close();
    }

    @Test
    public void test3(){
        //保存文章
        Jedis jedis = new Jedis("localhost");
        Post post = new Post();
        post.setAuthor("zzy");
        post.setContent("我的博客");
        post.setTitle("博客");
        Long postId = SavePost(post,jedis);
        GetPost(postId,jedis);
        Post post1 = updateTitle(postId, jedis);
        System.out.println(post1);
        deleteBlog(postId,jedis);
        jedis.close();
    }

    //保存博客
    public Long SavePost(Post post,Jedis jedis){
        Long postId = jedis.incr("posts");
        String myPost = JSON.toJSONString(post);
        jedis.set("post:"+postId+":data",myPost);
        return postId;
    }

    //获取博客
    public Post GetPost(Long postId,Jedis jedis){
        String getPost = jedis.get("post:" + postId + ":data");
        jedis.incr("post:" + postId + ":page.view");
        Post parseObject = JSON.parseObject(getPost, Post.class);
        System.out.println("第"+postId+"篇文章"+parseObject);
        return parseObject;
    }

    //修改标题
    public Post updateTitle(Long postId,Jedis jedis){
        Post post = GetPost(postId, jedis);
        post.setTitle("更改后的标题");
        String myPost = JSON.toJSONString(post);
        jedis.set("post:"+postId+":data",myPost);
        System.out.println("修改完成");
        return post;
    }
    //删除文章
    public void deleteBlog(Long postId,Jedis jedis){
        jedis.del("post:" + postId + ":data");
        jedis.del("post:"+postId+":page.view");
        System.out.println("删除成功");
    }
}
