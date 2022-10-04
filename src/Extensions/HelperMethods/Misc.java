package Extensions.HelperMethods;

public class Misc {

    static char[] alphabet = new char[]{
            'a','b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z',
            'A','B','C','D','E','F','G','H','I','J','K','L','M',
            'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    /**
     * For variable notation.
     * @param index Index of the variable alphabet; 0-25 = lowercase a-z | 26-51 = uppercase A-Z. Numbers up to 51 are valid.
     * @return Letter corresponding to index (e.g. 0 = 'a', 3 = 'd', etc.)
     */
    public static char GetAlphabetCode(int index){

        return alphabet[index];
    }

    public static int GetNumberCode(char alphabetCharacter){
        for(int i = 0; i <alphabet.length; i++){
            if(alphabet[i] == alphabetCharacter)
                return i;
        }
        // if this happens, something's (possibly) gone horribly wrong
        return -1;
    }
}
