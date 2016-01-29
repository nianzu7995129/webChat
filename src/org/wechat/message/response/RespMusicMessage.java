package org.wechat.message.response;


/*响应音乐消息*/

public class RespMusicMessage extends RespBaseMessage {

	// 音乐  
    private Music Music;  
  
    public Music getMusic() {  
        return Music;  
    }  
  
    public void setMusic(Music music) {  
        Music = music;  
    }  
	
}
