package com.autouploader.bot.Functionality;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Constants {
    static HashMap<String, String> listOfEmojisAndBosses = new HashMap<String, String>(){
        {
            put("Vale Guardian", "<:valeguardian:1027914648361644053>");
            put("Gorseval", "<:gorseval:1027914649825456128>");
            put("Sabetha the Saboteur", "<:sabetha:1027914651075346483>");
            put("Slothasor", "<:slothasor:1027914668053897266>");
            put("Bandit Trio", "<:trio:1027914665394720869>");
            put("Matthias Gabrel", "<:matthias:1027914666875297843>");
            put("McLeod the Silent", "<:escort:1062388796051705956>");
            put("Keep Construct", "<:kc:1027914683996454972>");
            put("Twisted Castle", "<:twistedcastle:1027914681203052585>");
            put("Xera", "<:xera:1027914682641698926>");
            put("Cairn the Indomitable", "<:cairn:1027914701755125840>");
            put("Mursaat Overseer", "<:mursaat:1027914699037229076>");
            put("Samarog", "<:samarog:1027914700396175441>");
            put("Deimos", "<:deimos:1027914697367887942>");
            put("Soulless Horror", "<:soullesshorror:1027914720411406367>");
            put("Desmina Escort", "<:river:1027914718666575885>");
            put("Broken King", "<:brokenking:1027914722189770863>");
            put("Soul Eater", "<:eaterofsouls:1027914725515874375>");
            put("Eye of Judgement", "<:eyes:1027914717634764851>");
            put("Dhuum", "<:dhuum:1027914723875885087>");
            put("Conjured Amalgamate", "<:ca:1027914735435386931>");
            put("Twin Largos", "<:largos:1027914737046016080>");
            put("Cardinal Adina", "<:adina:1027914748097998969>");
            put("Cardinal Sabir", "<:sabir:1027914746772606976>");
            put("Qadim the Peerless", "<:qtp:1027914749662482492>");
            put("Qadim", "<:qadim:1027914734000939038>");
            put("Elemental Ai, Keeper of the Peak", "<:lightai:1027914790951198750>");
            put("Dark Ai, Keeper of the Peak", "<:darkai:1027914792180121640>");
            put("Skorvald", "<:skorvald:1027914778594783252>");
            put("Artsariiv", "<:artsariiv:1027914781153316894>");
            put("Arkk", "<:arkk:1027914779790151721>");
            put("MAMA", "<:mama:1027914765927989298>");
            put("Nightmare Oratuss", "<:siax:1027914767274360913>");
            put("Ensolyss of the Endless Torment", "<:ensolyss:1027914768717201428>");
            put("The Voice and The Claw", "<:voiceandclaw:1027914842692124672>");
            put("Fraenir of Jormag", "<:fraenir:1027914839009534002>");
            put("Boneskinner", "<:boneskinner:1027914845540057240>");
            put("Whisper of Jormag", "<:whipserofjormag:1027914844046901299>");
            put("Icebrood Construct", "<:construct:1027914836279046194>");
            put("Cold War", "<:coldwar:1027914847167459348>");
            put("Forging Steel", "<:forgingsteel:1027914837545717780>");
            put("Aetherblade Hideout", "<:maitrin:1027914811713015921>");
            put("Minister Li", "<:ministerli:1027914813424287867>");
            put("Ankka", "<:ankka:1027914815001346088>");
            put("The Dragonvoid", "<:dragonvoid:1027914824862158858>");
            put("Standard Kitty Golem", "<:golem:1029048401972707388>");
        }
    };

    public static Image image;

    static {
        try {
			URL url = new URL("https://i.imgur.com/fEp1YSh.png");
			image =  new ImageIcon(url).getImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    static HashMap<String, ArrayList<String>> listOfBossesAndWings = new HashMap<String, ArrayList<String>>(){
        {
            put("Spirit Vale", new ArrayList<String>() {
                {
                    add("Vale Guardian");
                    add("Gorseval");
                    add("Sabetha the Saboteur");
                }
            });

            put("Salvation Pass", new ArrayList<String>() {
                {
                    add("Slothasor");
                    add("Bandit Trio");
                    add("Matthias Gabrel");
                }
            });

            put("Stronghold of the Faithful", new ArrayList<String>() {
                {
                    add("McLeod the Silent");
                    add("Keep Construct");
                    add("Twisted Castle");
                    add("Xera");
                }
            });

            put("Bastion of the Penitent", new ArrayList<String>() {
                {
                    add("Cairn the Indomitable");
                    add("Mursaat Overseer");
                    add("Samarog");
                    add("Deimos");
                }
            });

            put("Hall of Chains", new ArrayList<String>() {
                {
                    add("Soulless Horror");
                    add("Desmina Escort");
                    add("Broken King");
                    add("Soul Eater");
                    add("Eye of Judgement");
                    add("Dhuum");
                }
            });
                                
            put("The Key of Ahdashim", new ArrayList<String>() {
                {
                    add("Cardinal Adina");
                    add("Cardinal Sabir");
                    add("Qadim the Peerless");
                }
            });

            put("Mythwright Gambit", new ArrayList<String>() {
                {
                    add("Conjured Amalgamate");
                    add("Twin Largos");
                    add("Qadim");
                }
            });


            put("Sunqua Peak", new ArrayList<String>() {
                {
                    add("Elemental Ai, Keeper of the Peak");
                    add("Dark Ai, Keeper of the Peak");
                }
            });

            put("Shattered Observatory", new ArrayList<String>() {
                {
                    add("Skorvald");
                    add("Artsariiv");
                    add("Arkk");
                }
            });

            put("Nightmare", new ArrayList<String>() {
                {
                    add("MAMA");
                    add("Nightmare Oratuss");
                    add("Ensolyss of the Endless Torment");
                }
            });

            put("Practice Room", new ArrayList<String>() {
                {
                    add("Standard Kitty Golem");
                    add("Medium Kitty Golem");
                    add("Large Kitty Golem");
                }
            });

            put("Icebrood Saga", new ArrayList<String>() {
                {
                    add("The Voice and The Claw");
                    add("Fraenir of Jormag");
                    add("Boneskinner");
                    add("Whisper of Jormag");
                    add("Icebrood Construct");
                    add("Cold War");
                    add("Forging Steel");
                }
            });

            put("End of Dragons", new ArrayList<String>() {
                {
                    add("Aetherblade Hideout");
                    add("Ankka");
                    add("Minister Li");
                    add("The Dragonvoid");
                }
            });
        }
    };
}
