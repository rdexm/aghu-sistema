package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * ORADB PROCEDURE RN_CRGP_VER_BLOQUEIO
 * 
 * @author aghu
 * 
 */
@Stateless
public class VerificaBloqueioCirurgiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VerificaBloqueioCirurgiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcBloqSalaCirurgicaDAO mbcBloqSalaCirurgicaDAO;


	@EJB
	private AgendaProcedimentosFuncoesON agendaProcedimentosFuncoesON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8689219265760688775L;

	public enum VerificaBloqueioCirurgiaRNExceptionCode implements BusinessExceptionCode {
		MBC_01226;
	}

	/**
	 * ORADB PROCEDURE RN_CRGP_VER_BLOQUEIO
	 * 
	 * Verifica se a sala está bloqueada para o horário informado
	 * 
	 * @param dataCirurgia
	 * @param salaCirurgica
	 * @param dthrPrevInicio
	 * @param dthrInicioOrdem
	 * @param dthrPrevFim
	 * @param mbcProfAtuaUnidCirgs
	 * @throws BaseException
	 */
	public void verificarBloqueio(final Date dataCirurgia, final MbcSalaCirurgica salaCirurgica, final Date dthrPrevInicio, final Date dthrInicioOrdem, final Date dthrPrevFim,
			final MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) throws BaseException {

		Short sciUnfSeq = null;
		Short sciSeqp = null;

		if (salaCirurgica != null) {
			sciUnfSeq = salaCirurgica.getId().getUnfSeq();
			sciSeqp = salaCirurgica.getId().getSeqp();
		}

		// Pesquisa bloqueio de sala cirúrgicas
		List<MbcBloqSalaCirurgica> listaBloqSalaCirurgica = getMbcBloqSalaCirurgicaDAO().pesquisarBloqueioSalaCirugicaInserirAgenda(sciUnfSeq, sciSeqp, dataCirurgia);

		for (MbcBloqSalaCirurgica bloqSalaCirurgica : listaBloqSalaCirurgica) {

			if (this.validarProfissionalAtuaUnidadeCirurgica(bloqSalaCirurgica.getMbcProfAtuaUnidCirgs(), mbcProfAtuaUnidCirgs)) {

				DominioDiaSemanaSigla diaSemana = bloqSalaCirurgica.getDiaSemana();
				MbcTurnos turno = bloqSalaCirurgica.getTurno();
				DominioDiaSemanaSigla diaSemanaData = this.obterDiaSemanaSigla(dataCirurgia); // DECODE(TO_CHAR(C_DATA,'d'),'1','...

				if (diaSemana == null && turno == null) {
					// Sala está bloqueada para o horário informado
					throw new ApplicationBusinessException(VerificaBloqueioCirurgiaRNExceptionCode.MBC_01226);
				}

				if (diaSemana != null && turno == null) {

					if (CoreUtil.igual(diaSemana, diaSemanaData)) {
						// Sala está bloqueada para o horário informado
						throw new ApplicationBusinessException(VerificaBloqueioCirurgiaRNExceptionCode.MBC_01226);
					}

				}

				Date valorDthrInicioBloqueio = null;
				Date valorDthrFimBloqueio = null;
				Date valorDthrInicioCirg = null;
				Date valorDthrFimCirg = null;

				if (turno != null) {

					MbcHorarioTurnoCirg horarioTurnoCirg = this.getMbcHorarioTurnoCirgDAO().obterPorChavePrimaria(new MbcHorarioTurnoCirgId(sciUnfSeq, turno.getTurno()));

					valorDthrInicioBloqueio = this.getAgendaProcedimentosFuncoesON().getConcatenacaoDataHorario(dataCirurgia, horarioTurnoCirg.getHorarioInicial());
					valorDthrFimBloqueio = this.getAgendaProcedimentosFuncoesON().getConcatenacaoDataHorario(dataCirurgia, horarioTurnoCirg.getHorarioFinal());
					valorDthrInicioCirg = (Date) CoreUtil.nvl(dthrPrevInicio, dthrInicioOrdem);
					valorDthrFimCirg = (Date) CoreUtil.nvl(dthrPrevFim, dthrInicioOrdem);
				}

				if (diaSemana == null && turno != null) {

					if (this.validarDatasDiaSemanaTurno(valorDthrInicioBloqueio, valorDthrFimBloqueio, valorDthrInicioCirg, valorDthrFimCirg)) {
						// Sala está bloqueada para o horário informado
						throw new ApplicationBusinessException(VerificaBloqueioCirurgiaRNExceptionCode.MBC_01226);
					}

				}

				if (diaSemana != null && turno != null) {

					if (CoreUtil.igual(diaSemana, diaSemanaData)
							&& this.validarDatasDiaSemanaTurno(valorDthrInicioBloqueio, valorDthrFimBloqueio, valorDthrInicioCirg, valorDthrFimCirg)) {
						// Sala está bloqueada para o horário informado
						throw new ApplicationBusinessException(VerificaBloqueioCirurgiaRNExceptionCode.MBC_01226);
					}

				}
			}

		}

	}

	/**
	 * Parte EXTERNA do cursos C_BSC relacionada a pesquisa de profissionais da unidade cirúrgica. Vide: NVL(BSC.PUC_SER_MATRICULA,C_PUC_SER_MATRICULA)
	 * 
	 * @param profissional1
	 * @param profissional2
	 * @return
	 */
	private boolean validarProfissionalAtuaUnidadeCirurgica(MbcProfAtuaUnidCirgs profissional1, MbcProfAtuaUnidCirgs profissional2) {
		MbcProfAtuaUnidCirgs profissional = profissional1 == null ? profissional2 : profissional1;
		if (!CoreUtil.igual(profissional, profissional2)) {
			return false;
		}
		return true;
	}

	/**
	 * Obtem a sigla de um dia da semana (DominioDiaSemanaSigla) através de uma data de origem
	 * 
	 * @param data
	 * @return
	 */
	private DominioDiaSemanaSigla obterDiaSemanaSigla(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		int numeroDiaSemana = calendar.get(Calendar.DAY_OF_WEEK);
		return DominioDiaSemanaSigla.getDiaSemanaSigla(numeroDiaSemana);
	}

	/**
	 * 
	 * Parte REUTILIZADA pra a validação de datas: DIA DA SEMANA E TURNOS
	 * 
	 * @param valorDthrInicioBloqueio
	 * @param valorDthrFimBloqueio
	 * @param valorDthrInicioCirg
	 * @param valorDthrFimCirg
	 * @return
	 */
	private boolean validarDatasDiaSemanaTurno(Date valorDthrInicioBloqueio, Date valorDthrFimBloqueio, Date valorDthrInicioCirg, Date valorDthrFimCirg) {

		final boolean validacao1 = DateUtil.validaDataMaiorIgual(valorDthrInicioBloqueio, valorDthrInicioCirg)
				&& DateUtil.validaDataMenor(valorDthrInicioBloqueio, valorDthrFimCirg);
		final boolean validacao2 = DateUtil.validaDataMaior(valorDthrFimBloqueio, valorDthrInicioCirg) && DateUtil.validaDataMenor(valorDthrFimBloqueio, valorDthrFimCirg);
		final boolean validacao3 = DateUtil.validaDataMaiorIgual(valorDthrInicioCirg, valorDthrInicioBloqueio) && DateUtil.validaDataMenor(valorDthrInicioCirg, valorDthrFimBloqueio);
		final boolean validacao4 = DateUtil.validaDataMaior(valorDthrFimCirg, valorDthrInicioBloqueio) && DateUtil.validaDataMenor(valorDthrFimCirg, valorDthrFimBloqueio);

		return validacao1 || validacao2 || validacao3 || validacao4;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected MbcBloqSalaCirurgicaDAO getMbcBloqSalaCirurgicaDAO() {
		return mbcBloqSalaCirurgicaDAO;
	}

	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}

	private AgendaProcedimentosFuncoesON getAgendaProcedimentosFuncoesON() {
		return agendaProcedimentosFuncoesON;
	}

}
