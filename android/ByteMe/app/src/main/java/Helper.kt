class Helper {

    companion object {
        fun timeToString(inputTime: Int): String {
            var time = inputTime
            var stringTime = ""

            if (time > 3599) {
                stringTime += time / 3600
                stringTime += ":"

                time = time % 3600
            }
            if (time > 59) {
                stringTime += time / 60
                stringTime += ":"

                time = time % 60
            } else {
                stringTime += "00:"
            }
            if (time < 10) {
                stringTime += "0"
                stringTime += time
            } else {
                stringTime += time
            }


            return stringTime
        }
    }

}