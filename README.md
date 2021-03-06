# ACT Editor                                                                       
                                                                                   
This is a code editor for the ACT language created using eclipse plugin development kit. The plugin requires
at least Java 11. 

# Installation                                                                     
                                                                                   
Install the [Eclipse IDE](https://www.eclipse.org/downloads/). Eclipse may alse be installed with  [homebrew](https://brew.sh/)                         
                                                                                   
```bash                                                                            
brew install --cask eclipse-jee                                                    
```                                                                                
                                                                                   
# Using the Plugin                                                                 


* Download the ACTEditor JAR file.

* Locate the Eclipse plugins folder. For Mac users it may be in `/Applications/Eclipse\ JEE.app/Contents/Eclipse/plugins`

* Place the JAR file in the eclipse plugins folder and run eclipse with the `-clean` flag. 

```bash
/Applications/Eclipse\ JEE.app/Contents/MacOS/eclipse -clean
```

After the plugin is installed, Eclipse might still open ACT files with a generic text editor. To correct this,

* Right-click on the file in the Project Explorer.
 
* Go to **Open With > Other**

* Select the ACT Editor and check the **Use it for all \*.act files** option. 


                                                                                   
# Feedback                                                                         
                                                                                   
Please [let me know](mailto:mawuli.akpalu@yale.edu) if you have any suggestions.
                                                                                   
