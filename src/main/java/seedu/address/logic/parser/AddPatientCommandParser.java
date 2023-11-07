package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BLOODTYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CONDITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMERGENCY_CONTACT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GENDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NRIC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddPatientCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.person.Address;
import seedu.address.model.person.BloodType;
import seedu.address.model.person.Condition;
import seedu.address.model.person.Email;
import seedu.address.model.person.Gender;
import seedu.address.model.person.Ic;
import seedu.address.model.person.Name;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddPatientCommand object
 */
public class AddPatientCommandParser implements Parser<AddPatientCommand> {
    private static final Logger logger = LogsCenter.getLogger(AddPatientCommandParser.class);
    /**
     * Parses the given {@code String} of arguments in the context of the AddPatientCommand
     * and returns an AddPatientCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddPatientCommand parse(String args) throws ParseException {
        logger.fine("Attempting to parse AddPatientCommand from arguments: " + args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_REMARK, PREFIX_GENDER, PREFIX_NRIC, PREFIX_CONDITION, PREFIX_BLOODTYPE,
                        PREFIX_EMERGENCY_CONTACT);
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_GENDER,
                PREFIX_NRIC, PREFIX_CONDITION, PREFIX_BLOODTYPE, PREFIX_EMERGENCY_CONTACT)
                || !argMultimap.getPreamble().isEmpty()) {
            logger.warning("Invalid command format for AddPatientCommand: " + args);
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddPatientCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                PREFIX_GENDER, PREFIX_NRIC, PREFIX_CONDITION, PREFIX_BLOODTYPE, PREFIX_EMERGENCY_CONTACT);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Phone emergencyContact =
                ParserUtil.parsePhone(argMultimap.getValue(PREFIX_EMERGENCY_CONTACT).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Remark remark = new Remark(""); // add command does not allow adding remarks straight away
        Gender gender = ParserUtil.parseGender(argMultimap.getValue(PREFIX_GENDER).get());
        Ic ic = ParserUtil.parseIc(argMultimap.getValue(PREFIX_NRIC).get());
        BloodType bloodType = ParserUtil.parseBloodType(argMultimap.getValue(PREFIX_BLOODTYPE).get());
        Condition condition = ParserUtil.parseCondition(argMultimap.getValue(PREFIX_CONDITION).get());
        Set<Tag> tagList = ParserUtil.parsePatientTags(argMultimap.getAllValues(PREFIX_TAG));
        // appointments need to be added separately, so we initialise patients with empty appointments
        Set<Appointment> appointmentList = new HashSet<>();
        if (phone.equals(emergencyContact)) {
            throw new ParseException("Patient's phone number cannot be the same as their emergency contact.");
        }
        Patient patient =
                new Patient(name, phone, emergencyContact, email, address, remark, gender, ic, condition, bloodType,
                        appointmentList, tagList);

        logger.info("Successfully parsed AddPatientCommand with patient: " + patient);
        return new AddPatientCommand(patient);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
