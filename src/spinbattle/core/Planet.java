package spinbattle.core;

import levelgen.MarioReader;
import math.Vector2d;
import spinbattle.params.Constants;
import spinbattle.params.SpinBattleParams;
import spinbattle.view.ParticleEffect;

import spinbattle.core.SpinGameState;


public class Planet {
    public Vector2d position;
    public double rotation;
    public double rotationRate;
    public int index;

    public double growthRate;

    public double shipCount;
    public int ownedBy;
    SpinBattleParams params;
    Transporter transit;

    SpinGameState gameState;

    public Planet copy() {
        Planet planet = new Planet();
        // shallow copy position on the assumption that it will not change
        planet.position = position;
        planet.rotation = rotation;
        planet.rotationRate = rotationRate;
        planet.growthRate = growthRate;
        planet.shipCount = shipCount;
        planet.ownedBy = ownedBy;
        planet.params = params;
        planet.index = index;
        if (transit !=  null)
            planet.transit = transit.copy();
        return planet;
    }

    Planet processIncoming(double incomingShips, int playerId, SpinGameState gameState) {
        if (ownedBy != playerId) {
            // this is an invasion
            if (ownedBy == Constants.deathPlayer){
                shipCount = 0;
            }
            else {
                // decrement the ships, then set to id of incoming player and invert sign if it has gone negative
                shipCount -= incomingShips;
                if (shipCount <= 0) {
                    ownedBy = playerId;
                    shipCount = Math.abs(shipCount);
                    // and should make a transporter
                    // transit = getTransporter();
                }
            }
        } else {
            // must be owned by this player already, so add to the tally
            shipCount += incomingShips;
        }
        return this;
    }

    // todo: Probably here the trajectory update and termination needs to be done
    public Planet update(SpinGameState gameState) {

        if (ownedBy != Constants.neutralPlayer && ownedBy != Constants.deathPlayer) { //if it isn't neutral
            shipCount += growthRate;

            if (params.upperLimit){
                //check if the players have more than half of the ships in the game
                if(gameState.getPlayerShips(Constants.playerOne)+gameState.getPlayerShips(Constants.playerTwo)>
                        gameState.getAllShips()/2){

/*
                    System.out.println("having more than one third of the total ships");
                    System.out.println( gameState.getAllShips() + " - "+ gameState.getPlayerShips(Constants.playerOne)+
                            " - " +gameState.getPlayerShips(Constants.playerTwo));
*/
                    //calculate a threshold: fist planets ships+second planets ships / 3 or something like that
                    double threshold =
                            (gameState.getPlayerShips(Constants.playerOne) + gameState.getPlayerShips(Constants.playerTwo)) /
                                (gameState.getPlayerPlanets(Constants.playerOne)+ gameState.getPlayerPlanets(Constants.playerTwo)) ;

                    if(shipCount>threshold){
                       //remove the extra ships from the current planet
                        double removedShips = shipCount-threshold;
                        shipCount = removedShips;

                        //move the removed ships to the neutral planets
                        if (params.makeExtraShipsNeutral){
                            //check if there are any neutral planets left
                            if (gameState.singleOwner() != null) {
                                boolean foundNeutralPlanet = false;
                                while (!foundNeutralPlanet) {
                                    //random planet id
                                    int randPlanetId = params.getRandom().nextInt(gameState.planets.size());

                                    //check it is neutral, and if so, give it the extra ships and set the flag to true
                                    if (gameState.planets.get(randPlanetId).ownedBy == Constants.neutralPlayer) {
                                        gameState.planets.get(randPlanetId).shipCount += removedShips;
                                        foundNeutralPlanet = true;
                                    }
                                }

                            }
                        }



                        System.out.println("ship owned by "+ ownedBy+", removed "+ shipCount/2);
                        //ownedBy = Constants.neutralPlayer;
                    }
                }

            }

        }
        if (transit != null && transit.inTransit()) {
            transit.next(gameState.vectorField);
            // check to see whether it has arrived and if so return the target
            Planet destination = params.getCollider().getPlanetInRange(gameState, transit);

            if (destination != null) {
                // process the inbound
                destination.processIncoming(transit.payload, transit.ownedBy, gameState);
                transit.terminateJourney();
                if (gameState.logger != null) {
                    ParticleEffect effect = new ParticleEffect().setPosition(destination.position);
                    effect.setNParticle(20);
                    // System.out.println(effect);
                    gameState.logger.logEffect(effect);
                }
                // transit
                // System.out.println("Terminated Journey: " + transit.inTransit());
            }
        }
        rotation += rotationRate;
        return this;
    }

    public Planet setParams(SpinBattleParams params) {
        this.params = params;
        if (transit != null) {
            transit.setParams(params);
        }
        return this;
    }

    public Planet setIndex(int index) {
        this.index = index;
        return this;
    }

    Planet setRandomLocation(SpinBattleParams p) {
        position = new Vector2d(p.getRandom().nextDouble() * p.width, p.getRandom().nextDouble() * p.height);
        return this;
    }

    Planet setOwnership(int ownedBy) {
        this.ownedBy = ownedBy;
        // also set initial ships

//        shipCount = params.minInitialShips +
//                params.getRandom().nextInt(params.maxInitialShips - params.minInitialShips);

        //if owned by black holes
        if (ownedBy == Constants.deathPlayer){
            shipCount= 0;
        }
        else{
            shipCount = params.minInitialShips +
                    params.getRandom().nextInt(params.maxInitialShips - params.minInitialShips);
        }

        return this;
    }

    public Planet setRandomGrowthRate() {
        growthRate = params.getRandom().nextDouble() * (params.maxGrowth - params.minGrowth) + params.minGrowth;
        // also set a random rotation rate
        rotationRate = params.spinRatio * (params.getRandom().nextDouble() + 1);
        if (params.getRandom().nextDouble() < 0.5) rotationRate = -rotationRate;
        rotationRate *= Math.PI * 2.0 / 100;
        return this;
    }

    public int getRadius() {
        return (int) (Constants.growthRateToRadius * growthRate);
    }

    public String toString() {return position + " : " + ownedBy + " : " + getRadius();
    }


    public boolean transitReady() {
        return getTransporter() != null && !getTransporter().inTransit();
    }

    public Transporter getTransporter() {
        //introduced the death player/black hole
        // neutral planets AND black holes cannot release transporters
        if (ownedBy == Constants.neutralPlayer || ownedBy == Constants.deathPlayer) return null;
        // if transit is null then make a new one
        if (transit == null)
            transit = new Transporter().setParent(index).setParams(params);
        return transit;
    }

    public int getScore() {
        //introduced the deathplayer
        if (ownedBy == Constants.neutralPlayer || ownedBy == Constants.deathPlayer) return 0;
        int score = 0;
        if (ownedBy == Constants.playerOne) score = (int) shipCount;
        if (ownedBy == Constants.playerTwo) score = (int) -shipCount;
        return score;
    }

    public double mass() {
        return getRadius() * getRadius();
    }
}
