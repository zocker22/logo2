/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Timer;

import logo.LoGoApp;

/**
 *
 * @author steven
 */
public class CountdownPeriodenZeitSchwarz extends Countdown {

    public CountdownPeriodenZeitSchwarz( boolean starteSofort ){
        super( starteSofort );
    }

    public CountdownPeriodenZeitSchwarz( boolean starteSofort, long remainingTime ){
        super(true, remainingTime);
    }
    
    @Override
    protected void doWhenCountdownFinished() {
        LoGoApp.meineOberflaeche.setAnzeigePeriodenZeitSchwarz( 0 );
        LoGoApp.meineSteuerung.zeitAbgelaufenSchwarzPeriodenzeit();
    }

    @Override
    protected void doEverySecondTimerRuns() {
        LoGoApp.meineOberflaeche.setAnzeigePeriodenZeitSchwarz( remainingTime );
    }

}
