/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedWriter;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.DatabaseTestUtility.loadHsqldbConfig;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.flattenCommandlineArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.Main;
import schemacrawler.schemacrawler.Config;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.options.TextOutputFormat;
import sf.util.IOUtility;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class CommandLineTest
{

  private static final String COMMAND_LINE_OUTPUT = "command_line_output/";

  @Test
  public void commandLineOverridesWithConfig(final TestContext testContext,
                                             final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", ".*");
    args.put("routines", ".*");
    args.put("sequences", ".*");
    args.put("synonyms", ".*");

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(testContext, connectionInfo, args, config, "brief");
  }

  @Test
  public void commandLineRoutinesWithColumnsSorting(final TestContext testContext,
                                                    final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("routines", ".*");
    args.put("sortcolumns", Boolean.TRUE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineRoutinesWithoutColumnsSorting(final TestContext testContext,
                                                       final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("routines", ".*");
    args.put("sortcolumns", Boolean.FALSE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineRoutinesWithoutSorting(final TestContext testContext,
                                                final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("routines", ".*");
    args.put("sortroutines", Boolean.FALSE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineRoutinesWithSorting(final TestContext testContext,
                                             final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("tables", "");
    args.put("routines", ".*");
    args.put("sortroutines", Boolean.TRUE.toString());
    // Testing no tables, all routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineTablesWithColumnsSorting(final TestContext testContext,
                                                  final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sortcolumns", Boolean.TRUE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineTablesWithoutColumnsSorting(final TestContext testContext,
                                                     final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sortcolumns", Boolean.FALSE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineTablesWithoutSorting(final TestContext testContext,
                                              final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sorttables", Boolean.FALSE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineTablesWithSorting(final TestContext testContext,
                                           final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("routines", "");
    args.put("sorttables", Boolean.TRUE.toString());
    // Testing all tables, no routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineWithConfig(final TestContext testContext,
                                    final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put("schemacrawler.format.show_unqualified_names",
               Boolean.TRUE.toString());
    config.put("schemacrawler.table.pattern.include", ".*");
    config.put("schemacrawler.table.pattern.exclude", ".*A.*");
    config.put("schemacrawler.routine.pattern.include", ".*");
    config.put("schemacrawler.routine.pattern.exclude", ".*A.*");
    config.put("schemacrawler.sequence.pattern.include", ".*");
    config.put("schemacrawler.sequence.pattern.exclude", "");
    config.put("schemacrawler.synonym.pattern.include", ".*");
    config.put("schemacrawler.synonym.pattern.exclude", "");

    run(testContext, connectionInfo, args, config, "brief");
  }

  @Test
  public void commandLineWithDefaults(final TestContext testContext,
                                      final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("portablenames", Boolean.TRUE.toString());
    // Testing all tables, routines
    // Testing no sequences, synonyms

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineWithNonDefaults(final TestContext testContext,
                                         final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final Map<String, String> args = new HashMap<>();
    args.put("portablenames", Boolean.TRUE.toString());
    args.put("tables", "");
    args.put("routines", ".*");
    args.put("sequences", ".*");
    args.put("synonyms", ".*");

    run(testContext, connectionInfo, args, null, "brief");
  }

  @Test
  public void commandLineWithQueryCommand(final TestContext testContext,
                                          final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {

    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();

    run(testContext,
        connectionInfo,
        args,
        config,
        "SELECT * FROM BOOKS.Authors");
  }

  @Test
  public void commandLineWithQueryInConfig(final TestContext testContext,
                                           final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final String command = "query1";

    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put(command, "SELECT * FROM BOOKS.Books");

    run(testContext, connectionInfo, args, config, command);
  }

  @Test
  public void commandLineWithQueryOverInConfig(final TestContext testContext,
                                               final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    final String command = "query2";

    final Map<String, String> args = new HashMap<>();

    final Map<String, String> config = new HashMap<>();
    config.put(command, "SELECT ${columns} FROM ${table} ORDER BY ${columns}");

    run(testContext, connectionInfo, args, config, command);
  }

  private Path createConfig(final Map<String, String> config)
    throws IOException
  {
    final String prefix = "SchemaCrawler.TestCommandLineConfig";
    final Path configFile = IOUtility.createTempFilePath(prefix, "properties");
    final Properties configProperties = new Properties();
    configProperties.putAll(config);
    configProperties.store(newBufferedWriter(configFile, UTF_8), prefix);
    return configFile;
  }

  private void run(final TestContext testContext,
                   final DatabaseConnectionInfo connectionInfo,
                   final Map<String, String> argsMap,
                   final Map<String, String> config,
                   final String command)
    throws Exception
  {
    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout;)
    {
      argsMap.put("url", connectionInfo.getConnectionUrl());
      argsMap.put("user", "sa");
      argsMap.put("password", "");
      argsMap.put("noinfo", Boolean.TRUE.toString());
      argsMap.put("schemas", ".*\\.(?!FOR_LINT).*");
      argsMap.put("infolevel", "maximum");
      argsMap.put("command", command);
      argsMap.put("outputformat", TextOutputFormat.text.getFormat());
      argsMap.put("outputfile", out.toString());

      final Config runConfig = new Config();
      final Config informationSchema = loadHsqldbConfig();
      runConfig.putAll(informationSchema);
      if (config != null)
      {
        runConfig.putAll(config);
      }

      final Path configFile = createConfig(runConfig);
      argsMap.put("g", configFile.toString());

      Main.main(flattenCommandlineArgs(argsMap));
    }

    assertThat(outputOf(testout),
               hasSameContentAs(classpathResource(COMMAND_LINE_OUTPUT
                                                  + testContext.testMethodName()
                                                  + ".txt")));
  }

}
