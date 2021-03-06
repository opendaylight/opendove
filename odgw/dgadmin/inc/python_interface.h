/*
 * Copyright (c) 2010-2013 IBM Corporation
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
*/


/*
 * File:   python_lib.h
 * Author: Amitabha Biswas
 *
 * Created on Feb 25, 2012, 12:32 PM
 */

#include "include.h"

/**
 * \ingroup PythonInterface
 * @{
 */

#ifndef _PYTHON_LIB_H_
#define _PYTHON_LIB_H_

#define MAX_PYTHON_INTERPRETOR_PATH 256

extern int PythonLibLogLevel;

/*
 ******************************************************************************
 * process_cli_data --                                                    *//**
 *
 * \brief This is the routine that the PYTHON Scripts must call to execute CLI
 *        functions
 *
 * \param[in] self  PyObject
 * \param[in] args  The input MUST of the structure dps_client_data_t and
 *                  represented as a byte array
 *
 * \retval 0 Success
 * \retval -1 Failure
 *
 ******************************************************************************/
PyObject *process_cli_data(PyObject *self, PyObject *args);

/*
 ******************************************************************************
 * python_lib_get_instance --                                            *//**
 *
 * \brief This routine gets a handle to the instance of the PYTHON Class
 *
 * \param pythonpath - The Location of the PYTHON Module
 * \param module_file - The Module File Name
 * \param class_name - The Class Name
 * \param function_name - The Function Name
 * \param pyargs - The Arguments to Initialize the Instance of the Class With
 * \param ppy_instance - Location where the PYTHON Instance reference
 *                       will be stored
 *
 * \retval 0 DOVE_STATUS_OK
 * \retval Other Failure
 *
 *****************************************************************************/

dove_status python_lib_get_instance(char *pythonpath,
                                          const char *module_file,
                                          const char *class_name,
                                          PyObject *pyargs,
                                          PyObject **ppy_instance);

/*
 ******************************************************************************
 * python_lib_embed_cli_thread_start --                                   *//**
 *
 * \brief This routine gets the PYTHON request to send a message.
 *
 * \param fExitOnCtrlC Whether to exist on CTRL+C
 *
 * \return dove_status
 *
 *****************************************************************************/
dove_status python_lib_embed_cli_thread_start(int fExitOnCtrlC);

/** @} */

#endif // _PYTHON_LIB_H_
