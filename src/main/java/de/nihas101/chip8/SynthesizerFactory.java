package de.nihas101.chip8;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class SynthesizerFactory {
    public static Synthesizer createSynthesizer() throws MidiUnavailableException {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();

        return synthesizer;
    }
}
