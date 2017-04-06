package com.loopeer.android.photodrama4android.media.mediaio;


import com.loopeer.android.photodrama4android.media.model.Drama;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "Drama")
public class XmlDrama {

    @ElementList(name = "ImageClips")
    public ArrayList<XmlImageClip> imageClips;

    @ElementList(name = "Transitions")
    public ArrayList<XmlTransition> transitions;

    @ElementList(name = "SubtitleClips")
    public ArrayList<XmlSubtitleClip> subtitleClips;

    @ElementList(name = "MusicClips")
    public ArrayList<XmlMusicClip> musicClips;

    public XmlDrama(ArrayList<XmlImageClip> imageClips, ArrayList<XmlTransition> transitions
            , ArrayList<XmlSubtitleClip> subtitleClips, ArrayList<XmlMusicClip> musicClips) {
        this.imageClips = imageClips;
        this.transitions = transitions;
        this.subtitleClips = subtitleClips;
        this.musicClips = musicClips;
    }

    public Drama toDrama() {
        Drama drama = new Drama();
        if (imageClips != null && !imageClips.isEmpty()) {
            for (XmlImageClip imageClip : imageClips) {
                drama.videoGroup.imageClips.add(imageClip.toObject());
            }
        }
        if (transitions != null && !transitions.isEmpty()) {
            for (XmlTransition transition : transitions) {
                drama.videoGroup.transitionClips.add(transition.toObject());
            }
        }
        if (subtitleClips != null && !subtitleClips.isEmpty()) {
            for (XmlSubtitleClip subtitleClip : subtitleClips) {
                drama.videoGroup.subtitleClips.add(subtitleClip.toObject());
            }
        }
        if (musicClips != null && !musicClips.isEmpty()) {
            for (XmlMusicClip xmlMusicClip : musicClips) {
                drama.audioGroup.musicClips.add(xmlMusicClip.toObject());
            }
        }
        return drama;
    }
}
