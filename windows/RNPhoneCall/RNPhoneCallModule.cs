using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Phone.Call.RNPhoneCall
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNPhoneCallModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNPhoneCallModule"/>.
        /// </summary>
        internal RNPhoneCallModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNPhoneCall";
            }
        }
    }
}
