package edu.ncsu.csc.ase.dristi.audio;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

/**
 * Utility Class to speak the text code based on 
 * http://javaexamplemaven.blogspot.com/2013/01/after-spending-too-much-time-trying-to.html
 * @author rahul_pandita
 *
 */
public class Speech 
{
	private SynthesizerModeDesc desc;
	private Synthesizer synthesizer;
	
	private void init(String voiceName) throws EngineException, AudioException,
			EngineStateError, PropertyVetoException {
		if (desc == null) {
			System.setProperty("freetts.voices",
					"com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
			desc = new SynthesizerModeDesc(Locale.US);
			Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
			synthesizer = Central.createSynthesizer(desc);
			synthesizer.allocate();
			synthesizer.resume();
			SynthesizerModeDesc smd = (SynthesizerModeDesc) synthesizer
					.getEngineModeDesc();
			Voice[] voices = smd.getVoices();
			Voice voice = null;
			for (int i = 0; i < voices.length; i++) {
				if (voices[i].getName().equals(voiceName)) {
					voice = voices[i];
					break;
				}
			}
			synthesizer.getSynthesizerProperties().setVoice(voice);
		}
	}

	private void terminate() throws EngineException, EngineStateError {
		synthesizer.deallocate();
	}

	private void doSpeak(String speakText) throws EngineException,
			AudioException, IllegalArgumentException, InterruptedException {
		synthesizer.speakPlainText(speakText, null);
		synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
	}
	
	public static void speak(String text) throws Exception
	{
		Speech su = new Speech();
		su.init("kevin16");
		su.doSpeak(text);
		su.terminate();
	}
	
	public static void main(String[] args) throws Exception {
		Speech su = new Speech();
		su.init("kevin16");
		try 
		{
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			System.out.println("Enter The Sentnce =>");
			String s = br.readLine();
			while(s.length()>0)
			{
				su.doSpeak(s);
				System.out.println("Enter The Sentnce =>");
				s = br.readLine();
			}
			 
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		su.terminate();
	}

}
