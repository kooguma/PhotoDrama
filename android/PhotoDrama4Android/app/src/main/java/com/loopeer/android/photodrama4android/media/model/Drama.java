package com.loopeer.android.photodrama4android.media.model;

import com.laputapp.model.BaseModel;
import com.loopeer.android.photodrama4android.media.mediaio.XmlDrama;
import com.loopeer.android.photodrama4android.media.mediaio.XmlImageClip;
import com.loopeer.android.photodrama4android.media.mediaio.XmlMusicClip;
import com.loopeer.android.photodrama4android.media.mediaio.XmlSubtitleClip;
import com.loopeer.android.photodrama4android.media.mediaio.XmlTransition;
import com.loopeer.android.photodrama4android.media.utils.ClipsCreator;

import java.util.ArrayList;
import java.util.List;

public class Drama extends BaseModel {
    public AudioGroup audioGroup;
    public VideoGroup videoGroup;

    public Drama() {
        audioGroup = new AudioGroup();
        videoGroup = new VideoGroup();
    }

    public static Drama createFromPath(List<String> urls) {
        Drama drama = new Drama();
        VideoGroup videoGroup = new VideoGroup();
        videoGroup.imageClips = ClipsCreator.createImageClips(urls);
        videoGroup.transitionClips = ClipsCreator.createTransitionClips(videoGroup.imageClips);
        drama.videoGroup = videoGroup;
        return drama;
    }

    public int getShowTimeTotal() {
        if (videoGroup.imageClips.isEmpty()) return 0;
        ImageClip imageClip = videoGroup.imageClips.get(videoGroup.imageClips.size() - 1);
        return imageClip.getEndTime();
    }

    public XmlDrama toXml() {
        ArrayList<XmlImageClip> xmlImageClips = new ArrayList<>();
        ArrayList<XmlTransition> xmlTransitions = new ArrayList<>();
        ArrayList<XmlSubtitleClip> xmlSubtitleClips = new ArrayList<>();
        ArrayList<XmlMusicClip> xmlMusicClips = new ArrayList<>();

        for (ImageClip imageClip : videoGroup.imageClips) {
            XmlImageClip xmlImageClip = imageClip.toXml();
            xmlImageClips.add(xmlImageClip);
        }

        for (TransitionClip transitionClip : videoGroup.transitionClips) {
            XmlTransition xmlTransition = transitionClip.toXml();
            xmlTransitions.add(xmlTransition);
        }

        for (SubtitleClip subtitleClip : videoGroup.subtitleClips) {
            XmlSubtitleClip xmlSubtitleClip = subtitleClip.toXml();
            xmlSubtitleClips.add(xmlSubtitleClip);
        }

        for (MusicClip musicClip : audioGroup.musicClips) {
            XmlMusicClip xmlMusicClip = musicClip.toXml();
            xmlMusicClips.add(xmlMusicClip);
        }
        return new XmlDrama(xmlImageClips, xmlTransitions, xmlSubtitleClips, xmlMusicClips);
    }

    public List<String> getAudioPaths() {
        List<String> results = new ArrayList<>();
        for (MusicClip musicClip : audioGroup.musicClips) {
            results.add(musicClip.path);
        }
        return results;
    }

    public List<String> getImagePaths() {
        List<String> results = new ArrayList<>();
        for (ImageClip imageClip : videoGroup.imageClips) {
            results.add(imageClip.path);
        }
        return results;
    }

}
