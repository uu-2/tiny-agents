package com.uu2.tinyagents.core.audio;

import com.uu2.tinyagents.core.audio.transfer.AudioResponse;
import com.uu2.tinyagents.core.audio.transfer.Speech2TextRequest;
import com.uu2.tinyagents.core.audio.transfer.Text2SpeechRequest;

import java.io.File;

public interface AudioModel {

    AudioResponse Text2Speech(Text2SpeechRequest request);

    AudioResponse Speech2Text(Speech2TextRequest request);
}
