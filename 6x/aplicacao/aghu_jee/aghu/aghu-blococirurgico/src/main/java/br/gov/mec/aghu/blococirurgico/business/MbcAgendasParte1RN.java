package br.gov.mec.aghu.blococirurgico.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcAgendasRN.MbcAgendasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class MbcAgendasParte1RN  extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final String _PARA_ = " para ";

	private static final Log LOG = LogFactory.getLog(MbcAgendasParte1RN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;
	
	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;
	
	private static final long serialVersionUID = 4875765431228664613L;

	public void preAtualizarAgendaParte3(MbcAgendas agenda,
			MbcAgendas agendaOriginal) throws ApplicationBusinessException {
		if(CoreUtil.modificados(agenda.getEspProcCirgs().getId().getPciSeq(), agendaOriginal.getEspProcCirgs().getId().getPciSeq()) ||
				CoreUtil.modificados(agenda.getEspProcCirgs().getId().getEspSeq(), agendaOriginal.getEspProcCirgs().getId().getEspSeq()) ||
				CoreUtil.modificados(agenda.getQtdeProc(),agendaOriginal.getQtdeProc()) ||
				CoreUtil.modificados(zerarData(agenda.getTempoSala()),zerarData(agendaOriginal.getTempoSala())) ){
			verificarQuantidadeProcedimento(agenda.getEspProcCirgs(),agenda.getQtdeProc(),agenda.getTempoSala(),agenda.getIndGeradoSistema());
		}
		
		if(CoreUtil.modificados(agenda.getRegime(),agendaOriginal.getRegime())){
			atualizarConvenio(agenda);
		}
		
		if(CoreUtil.modificados(agenda.getIndSituacao(),agendaOriginal.getIndSituacao())){
			atualizarGeradoSistema(agenda);
		}
		
		if((CoreUtil.modificados(agenda.getIndSituacao(),agendaOriginal.getIndSituacao()) ||
				CoreUtil.modificados(agenda.getDtAgenda(),agendaOriginal.getDtAgenda())) &&
				DominioSituacaoAgendas.LE.equals(agenda.getIndSituacao()) || 
				DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao()) ||
				DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())){
			verificarObitoPaciente(agenda.getPaciente(),agenda.getIndGeradoSistema());
		}
	}
	
	public void verificarIncluirHistorico(MbcAgendas agenda,
			MbcAgendas agendaOriginal) throws BaseException {

		FatConvenioSaudePlano cpsOriginal = iFaturamentoFacade.obterFatConvenioSaudePlanoPorChavePrimaria(agendaOriginal.getConvenioSaudePlano().getId());
		MbcProfAtuaUnidCirgs p = mbcProfAtuaUnidCirgsDAO.obterPorChavePrimaria(agenda.getProfAtuaUnidCirgs().getId());
		MbcProfAtuaUnidCirgs pOriginal = mbcProfAtuaUnidCirgsDAO.obterPorChavePrimaria(agendaOriginal.getProfAtuaUnidCirgs().getId());
		
		if(
				CoreUtil.modificados(agenda.getUnidadeFuncional(),agendaOriginal.getUnidadeFuncional()) || 
				CoreUtil.modificados(agenda.getEspecialidade(),agendaOriginal.getEspecialidade()) ||
				CoreUtil.modificados(agenda.getRegime(),agendaOriginal.getRegime()) ||
				CoreUtil.modificados(agenda.getIndSituacao(),agendaOriginal.getIndSituacao()) ||
				CoreUtil.modificados(agenda.getIndExclusao(),agendaOriginal.getIndExclusao()) ||
				CoreUtil.modificados(agenda.getEspProcCirgs().getId(),agendaOriginal.getEspProcCirgs().getId()) ||
				CoreUtil.modificados(zerarData(agenda.getTempoSala()),zerarData(agendaOriginal.getTempoSala())) ||
				CoreUtil.modificados(agenda.getIndPrioridade(),agendaOriginal.getIndPrioridade()) ||
				CoreUtil.modificados(agenda.getIndPrecaucaoEspecial(),agendaOriginal.getIndPrecaucaoEspecial()) ||
				CoreUtil.modificados(agenda.getJustifPrioridade(),agendaOriginal.getJustifPrioridade()) ||
				CoreUtil.modificados(agenda.getComentario(),agendaOriginal.getComentario()) ||
				CoreUtil.modificados(agenda.getDthrPrevInicio(),agendaOriginal.getDthrPrevInicio()) ||
				CoreUtil.modificados(agenda.getDthrPrevFim(),agendaOriginal.getDthrPrevFim()) ||
				CoreUtil.modificados(agenda.getSalaCirurgica(),agendaOriginal.getSalaCirurgica()) ||
				CoreUtil.modificados(agenda.getConvenioSaudePlano(),cpsOriginal) ||
				!Objects.equals(p,pOriginal) ||
				CoreUtil.modificados(agenda.getDtAgenda(),agendaOriginal.getDtAgenda()) ||
				CoreUtil.modificados(agenda.getQtdeProc(),agendaOriginal.getQtdeProc()) ||
				CoreUtil.modificados(agenda.getIndGeradoSistema(),agendaOriginal.getIndGeradoSistema()) ||
				CoreUtil.modificados(agenda.getGrupo(),agendaOriginal.getGrupo()) ||
				CoreUtil.modificados(agenda.getLadoCirurgia(),agendaOriginal.getLadoCirurgia()) ||
				CoreUtil.modificados(agenda.getMaterialEspecial(),agendaOriginal.getMaterialEspecial())
					
		
		
		){
			getIncluirHistorico(agenda,agendaOriginal);
		}
	}
	
	/**
	 * Zera o dia, mês e ano da data por parametro
	 * @param date
	 * @param horas
	 * @param minutos
	 * @return
	 */
	public Date zerarData(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		
		Calendar dateAux = Calendar.getInstance();
		c1.set(Calendar.DAY_OF_MONTH, dateAux.get(Calendar.DAY_OF_MONTH));
		c1.set(Calendar.MONTH, dateAux.get(Calendar.MONTH));
		c1.set(Calendar.YEAR, dateAux.get(Calendar.YEAR));
		
		return c1.getTime();
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_qt_proc
	 * @param espProcCirgs
	 * @param qtdeProc
	 * @param tempoSala
	 * @param indGeradoSistema
	 * @throws ApplicationBusinessException 
	 * 
	 * Quantidade só pode ser maior que 1 quando for multiplo na tabela Proced. cirúrgico
     * Tempo de sala deve ser maior que o tempo mínimo do procedimento
	 */
	public void verificarQuantidadeProcedimento(
			MbcEspecialidadeProcCirgs espProcCirgs, Short qtdeProc,
			Date tempoSala, Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(indGeradoSistema){
			return;
		}

		if(espProcCirgs.getMbcProcedimentoCirurgicos() == null){
			throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00971);
		}else{
			MbcProcedimentoCirurgicos procedCirurgico = espProcCirgs.getMbcProcedimentoCirurgicos();
			Integer tempoMinimoMinutos = getMbcAgendasRN().gerarTempoMinimoMinutos(espProcCirgs.getMbcProcedimentoCirurgicos());
			String tempoMinimoEditado = getMbcAgendasRN().gerarTempoMinimoEditado(tempoMinimoMinutos);
			
			if(qtdeProc != null && qtdeProc > 1 && !procedCirurgico.getIndProcMultiplo()){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00972);
			} else if(qtdeProc != null && qtdeProc == 0){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00973);
			}
			
			if(tempoMinimoMinutos != null && (
					(tempoSala.getHours()*60 + tempoSala.getMinutes()) < tempoMinimoMinutos)){ 
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_01070,tempoMinimoEditado+" h do outro procedimento "+procedCirurgico.getDescricao());
			}
			
		}
			
		
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_atu_convenio
	 * @param agenda
	 * @throws ApplicationBusinessException 
	 * 
	 * Atualiza convênio/plano conforme regime para agenda não gerada pelo sistema de
     *      cirurgias
	 */
	public void atualizarConvenio(MbcAgendas agenda) throws ApplicationBusinessException {
		if(agenda.getIndGeradoSistema()){
			return;
		}
		
		AghParametros paramSusPlanoInternacao = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
		
		AghParametros paramSusPlanoAmbulatorio = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		
		AghParametros paramSusPadrao = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		
		FatConvenioSaude convSus = this.getFaturamentoFacade().obterFatConvenioSaudePorId(paramSusPadrao.getVlrNumerico().shortValue());
		
		if(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO.equals(agenda.getRegime()) ){
			FatConvenioSaudePlano plano = this.getFaturamentoFacade().obterFatConvenioSaudePlanoPorChavePrimaria(
					new FatConvenioSaudePlanoId(convSus.getCodigo(), paramSusPlanoAmbulatorio.getVlrNumerico().byteValue()));
			agenda.setConvenioSaudePlano(plano);
		}else{
			FatConvenioSaudePlano plano = this.getFaturamentoFacade().obterFatConvenioSaudePlanoPorChavePrimaria(
					new FatConvenioSaudePlanoId(convSus.getCodigo(), paramSusPlanoInternacao.getVlrNumerico().byteValue()));
			agenda.setConvenioSaudePlano(plano);
		}
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_atu_gera_sis
	 * @param agenda
	 * 
	 * Se troca situação para 'ES' então ind_gerado_sistema é atualizado para 'N'
	 * 
	 * Obs: Não está sendo enviado o parametro old.ind_situacao pois o mesmo não é utilizado na procedure migrada.
	 */
	public void atualizarGeradoSistema(MbcAgendas agenda) {
		if(DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao()) || DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())){
			agenda.setIndGeradoSistema(false);
		}
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_obt_pac
	 * @param paciente
	 * @param indGeradoSistema
	 * 
	 * Verifica se o paciente tem data de óbito informado
	 * @throws ApplicationBusinessException 
	 */
	public void verificarObitoPaciente(AipPacientes paciente,
			Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(indGeradoSistema){
			return;
		}
		
		if(paciente.getDtObito() != null || paciente.getDtObitoExterno() != null){
			throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_01321);
		}
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_inc_historic
	 * @param agenda
	 * @param agendaOriginal
	 * @param servidorLogado 
	 * @throws BaseException 
	 */
	private void getIncluirHistorico(MbcAgendas agenda, MbcAgendas agendaOriginal) throws BaseException {
		Boolean virgula = false;
		String virg = ", ";
		StringBuilder descricao = new StringBuilder();
		virgula = getIncluirHistoricoVerificarUnidadeFuncional(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarEspecialidade(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarRegime(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarIndSituacao(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarIndExclusao(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarEspProcCirgs(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarQtdeProc(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarEspProcCirgsQtdeProc(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarTempoSala(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = getIncluirHistoricoVerificarIndPrioridade(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarJustifPrioridade(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarIndPrecaucaoEspecial(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarGrupo(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarComentario(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarSalaCirurgica(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarDthrPrevInicio(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarDthrPrevFim(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarOrdemOverbooking(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarConvenioSaudePlano(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarProfAtuaUnidCirgs(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarDtAgenda(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarIndGeradoSistema(agenda,
				agendaOriginal, virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarLadoCirurgia(agenda, agendaOriginal,
				virgula, virg, descricao);		
		virgula = incluirHistoricoVerificarMaterialEspecial(agenda, agendaOriginal,
				virgula, virg, descricao);	
		
		if(descricao != null && !descricao.toString().isEmpty()){
			getMbcAgendaHistoricoRN().inserir(agendaOriginal.getSeq(), agenda.getIndSituacao(), DominioOrigem.A, StringUtils.trim(descricao.toString()), DominioOperacaoAgenda.A);
		}
	}

	private Boolean incluirHistoricoVerificarMaterialEspecial(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getMaterialEspecial(),agendaOriginal.getMaterialEspecial())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			descricao.append(" material especial foi alterado de ")
				.append(agendaOriginal.getMaterialEspecial() != null ? agendaOriginal.getMaterialEspecial() : "")
				.append(_PARA_).append(agenda.getMaterialEspecial() != null ? agenda.getMaterialEspecial() : "");
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarLadoCirurgia(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getLadoCirurgia(),agendaOriginal.getLadoCirurgia())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			
			if(agendaOriginal.getLadoCirurgia() == null && agenda.getLadoCirurgia() != null){
				descricao.append(" lado da cirurgia ").append(agenda.getLadoCirurgia());
			}			
			if(agendaOriginal.getLadoCirurgia() != null && agenda.getLadoCirurgia() != null){
				descricao.append(" lado da cirurgia foi alterado de ").append(agendaOriginal.getLadoCirurgia()).
				append(_PARA_).append(agenda.getLadoCirurgia());
			}	
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarIndGeradoSistema(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getIndGeradoSistema(),agendaOriginal.getIndGeradoSistema())){
			virgula = verificarVirgula(virgula, virg, descricao);			
			if(!agendaOriginal.getIndGeradoSistema() && agenda.getIndGeradoSistema()){
				descricao.append(" indicação de geração através do portal foi alterada para geração através do sistema de cirurgias");
			}			
			if(agendaOriginal.getIndGeradoSistema() && !agenda.getIndGeradoSistema()){
				descricao.append(" indicação de geração através do sistema de cirurgias foi alterada para geração através do portal");
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarDtAgenda(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getDtAgenda(),agendaOriginal.getDtAgenda())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			
			if(agendaOriginal.getDtAgenda() == null && agenda.getDtAgenda() != null){
				descricao.append(" data da agenda: ").append(df.format(agenda.getDtAgenda()));
			}	
			if(agendaOriginal.getDtAgenda() != null && agenda.getDtAgenda() != null){
				descricao.append(" data da agenda alterada de ").append(df.format(agendaOriginal.getDtAgenda())).
				append(_PARA_).append(df.format(agenda.getDtAgenda()));
			}			
			if(agendaOriginal.getDtAgenda() != null && agenda.getDtAgenda() == null){
				descricao.append(" data da agenda excluída: ").append(df.format(agendaOriginal.getDtAgenda()));
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarProfAtuaUnidCirgs(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		MbcProfAtuaUnidCirgs p = mbcProfAtuaUnidCirgsDAO.obterPorChavePrimaria(agenda.getProfAtuaUnidCirgs().getId());
		MbcProfAtuaUnidCirgs pOriginal = mbcProfAtuaUnidCirgsDAO.obterPorChavePrimaria(agendaOriginal.getProfAtuaUnidCirgs().getId());
		RapServidores s = registroColaboradorFacade.obterServidor(p.getRapServidores().getId().getVinCodigo(), p.getRapServidores().getId().getMatricula());
		RapServidores sOriginal = registroColaboradorFacade.obterServidor(pOriginal.getRapServidores().getId().getVinCodigo(), pOriginal.getRapServidores().getId().getMatricula());
		
		if(CoreUtil.modificados(p,pOriginal)){
			virgula = verificarVirgula(virgula, virg, descricao);	
				
			if(CoreUtil.modificados(s.getPessoaFisica().getNome(),sOriginal.getPessoaFisica().getNome())){
				descricao.append(" equipe alterada de ").append(sOriginal.getPessoaFisica().getNome()).
				append(_PARA_).append(s.getPessoaFisica().getNome());
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarConvenioSaudePlano(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		FatConvenioSaudePlano atual = this.iFaturamentoFacade.obterConvenioSaudePlano(agenda.getConvenioSaudePlano().getId().getCnvCodigo(), agenda.getConvenioSaudePlano().getId().getSeq());
		FatConvenioSaudePlano original = this.iFaturamentoFacade.obterConvenioSaudePlano(agendaOriginal.getConvenioSaudePlano().getId().getCnvCodigo(), agendaOriginal.getConvenioSaudePlano().getId().getSeq());
		
		if(!Objects.equals(atual,original)){
			virgula = verificarVirgula(virgula, virg, descricao);	
			
			if(original == null && atual != null){
				descricao.append(" convênio/plano incluído: ").append(atual.getConvenioSaude().getDescricao()).append(_HIFEN_).
				append(atual.getDescricao());
			}			
			if(original != null && atual != null){
				descricao.append(" convênio/plano alterado de ").append(original.getConvenioSaude().getDescricao()).append(_HIFEN_).
				append(original.getDescricao()).
				append(_PARA_).append(atual.getConvenioSaude().getDescricao()).append(_HIFEN_).
				append(atual.getDescricao());
			}			
			if(original != null && atual == null){
				descricao.append(" convênio/plano excluído: ").append(original.getConvenioSaude().getDescricao()).append(_HIFEN_).
				append(original.getDescricao());
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarOrdemOverbooking(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getOrdemOverbooking(),agendaOriginal.getOrdemOverbooking())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			if(agenda.getOrdemOverbooking() != null){
				descricao.append(" overbooking");
			}else{
				descricao.append(" overbooking excluído");
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarDthrPrevFim(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getDthrPrevFim(),agendaOriginal.getDthrPrevFim())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			if(agendaOriginal.getDthrPrevFim() == null && agenda.getDthrPrevFim() != null && agendaOriginal.getOrdemOverbooking() == null){
				descricao.append(" data prevista de fim: ").append(df.format(agenda.getDthrPrevFim()));
			}	
			if(agendaOriginal.getDthrPrevFim() == null && agenda.getDthrPrevFim() != null && agendaOriginal.getOrdemOverbooking() != null){
				descricao.append(" overbooking alterado para data prevista de fim: ").append(df.format(agenda.getDthrPrevFim()));
			}			
			if(agendaOriginal.getDthrPrevFim() != null && agenda.getDthrPrevFim() != null){
				descricao.append(" data prevista de fim alterada de ").append(df.format(agendaOriginal.getDthrPrevFim())).
				append(_PARA_).append(df.format(agenda.getDthrPrevFim()));
			}			
			if(agendaOriginal.getDthrPrevFim() != null && agenda.getDthrPrevFim() == null){
				descricao.append(" data prevista de fim excluída: ").append(df.format(agendaOriginal.getDthrPrevFim()));
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarDthrPrevInicio(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getDthrPrevInicio(),agendaOriginal.getDthrPrevInicio())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			if(agendaOriginal.getDthrPrevInicio() == null && agenda.getDthrPrevInicio() != null && agendaOriginal.getOrdemOverbooking() == null){
				descricao.append(" data prevista de início: ").append(df.format(agenda.getDthrPrevInicio()));
			}	
			if(agendaOriginal.getDthrPrevInicio() == null && agenda.getDthrPrevInicio() != null && agendaOriginal.getOrdemOverbooking() != null){
				descricao.append(" overbooking alterado para data prevista de início: ").append(df.format(agenda.getDthrPrevInicio()));
			}			
			if(agendaOriginal.getDthrPrevInicio() != null && agenda.getDthrPrevInicio() != null){
				descricao.append(" data prevista de início alterada de ").append(df.format(agendaOriginal.getDthrPrevInicio())).
				append(_PARA_).append(df.format(agenda.getDthrPrevInicio()));
			}			
			if(agendaOriginal.getDthrPrevInicio() != null && agenda.getDthrPrevInicio() == null){
				descricao.append(" data prevista de início excluída: ").append(df.format(agendaOriginal.getDthrPrevInicio()));
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarSalaCirurgica(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getSalaCirurgica(),agendaOriginal.getSalaCirurgica())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			
			if(agendaOriginal.getSalaCirurgica() == null && agenda.getSalaCirurgica() != null){
				descricao.append(" sala: ").append(agenda.getSalaCirurgica().getNome());
			}			
			if(agendaOriginal.getSalaCirurgica() != null && agenda.getSalaCirurgica() != null){
				descricao.append(" sala alterada de ").append(agendaOriginal.getSalaCirurgica().getNome()).
				append(_PARA_).append(agenda.getSalaCirurgica().getNome());
			}			
			if(agendaOriginal.getSalaCirurgica() != null && agenda.getSalaCirurgica() == null){
				descricao.append(" sala excluída: ").append(agendaOriginal.getSalaCirurgica().getNome());
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarComentario(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getComentario(),agendaOriginal.getComentario())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			
			if(agendaOriginal.getComentario() == null && agenda.getComentario() != null){
				descricao.append(" comentário incluído: ").append(agenda.getComentario());
			}			
			if(agendaOriginal.getComentario() != null && agenda.getComentario() != null){
				descricao.append(" comentário alterado de ").append(agendaOriginal.getComentario()).
				append(_PARA_).append(agenda.getComentario());
			}			
			if(agendaOriginal.getComentario() != null && agenda.getComentario() == null){
				descricao.append(" comentário excluído: ").append(agendaOriginal.getComentario());
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarGrupo(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getGrupo(),agendaOriginal.getGrupo())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			
			if(agendaOriginal.getGrupo() == null && agenda.getGrupo() != null){
				descricao.append(" grupo incluído: ").append(agenda.getGrupo());
			}			
			if(agendaOriginal.getGrupo() != null && agenda.getGrupo() != null){
				descricao.append(" grupo alterado de ").append(agendaOriginal.getGrupo()).
				append(_PARA_).append(agenda.getGrupo());
			}			
			if(agendaOriginal.getGrupo() != null && agenda.getGrupo() == null){
				descricao.append(" grupo excluído: ").append(agendaOriginal.getGrupo());
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarIndPrecaucaoEspecial(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getIndPrecaucaoEspecial(),agendaOriginal.getIndPrecaucaoEspecial())){
			virgula = verificarVirgula(virgula, virg, descricao);			
			if(!agendaOriginal.getIndPrecaucaoEspecial() && agenda.getIndPrecaucaoEspecial()){
				descricao.append(" indicação de precaução especial");
			}			
			if(agendaOriginal.getIndPrecaucaoEspecial() && !agenda.getIndPrecaucaoEspecial()){
				descricao.append(" indicação de precaução especial excluída");
			}
		}
		return virgula;
	}

	private Boolean incluirHistoricoVerificarJustifPrioridade(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getJustifPrioridade(),agendaOriginal.getJustifPrioridade()) && (agendaOriginal.getJustifPrioridade() != null || !"".equals(agendaOriginal.getJustifPrioridade()))){
			virgula = verificarVirgula(virgula, virg, descricao);
			descricao.append(" justificativa da prioridade alterada de ")
				.append(agendaOriginal.getJustifPrioridade() != null ? agendaOriginal.getJustifPrioridade() : "")
				.append(_PARA_).append(agenda.getJustifPrioridade() != null ? agenda.getJustifPrioridade() : "");				
		}
		return virgula;
	}

	private Boolean getIncluirHistoricoVerificarIndPrioridade(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getIndPrioridade(),agendaOriginal.getIndPrioridade())){
			virgula = verificarVirgula(virgula, virg, descricao);			
			if(!agendaOriginal.getIndPrioridade() && agenda.getIndPrioridade()){
				descricao.append(" incluído: prioridade de agendamento, justificativa: ").append(agenda.getJustifPrioridade());
			}			
			if(agendaOriginal.getIndPrioridade() && !agenda.getIndPrioridade()){
				descricao.append(" excluído: prioridade de agendamento e justificativa");
			}
		}
		return virgula;
	}

	private Boolean getIncluirHistoricoVerificarTempoSala(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(zerarData(agenda.getTempoSala()),zerarData(agendaOriginal.getTempoSala()))){
			virgula = verificarVirgula(virgula, virg, descricao);
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			descricao.append(" tempo de sala alterado de ").append(df.format(agendaOriginal.getTempoSala())).
			append(_PARA_).append(df.format(agenda.getTempoSala()));
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarEspProcCirgsQtdeProc(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		if( !CoreUtil.modificados(agenda.getEspProcCirgs(),agendaOriginal.getEspProcCirgs()) &&
				CoreUtil.modificados(agenda.getQtdeProc(),agendaOriginal.getQtdeProc())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			if(agendaOriginal.getQtdeProc() == null && agenda.getQtdeProc() != null){
				descricao.append(" quantidade incluída para o procedimento ").append(agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao()).
				append(": ").append(agenda.getQtdeProc());
			}			
			if(agendaOriginal.getQtdeProc() != null && agenda.getQtdeProc() != null){
				descricao.append(" quantidade do procedimento ").append(agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao()).
				append(" alterada de ").append(agendaOriginal.getQtdeProc()).
				append(_PARA_).append(agenda.getQtdeProc());
			}			
			if(agendaOriginal.getQtdeProc() != null && agenda.getQtdeProc() == null){
				descricao.append(" quantidade excluída do procedimento ").append(agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao()).
				append(": ").append(agendaOriginal.getQtdeProc());
			}
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarQtdeProc(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getEspProcCirgs(),agendaOriginal.getEspProcCirgs()) &&
				CoreUtil.modificados(agenda.getQtdeProc(),agendaOriginal.getQtdeProc())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			if(agendaOriginal.getQtdeProc() == null && agenda.getQtdeProc() != null){
				descricao.append(" quantidade incluída: ").append(agenda.getQtdeProc());
			}			
			if(agendaOriginal.getQtdeProc() != null && agenda.getQtdeProc() != null){
				descricao.append(" quantidade alterada de ").append(agendaOriginal.getQtdeProc()).
				append(_PARA_).append(agenda.getQtdeProc());
			}			
			if(agendaOriginal.getQtdeProc() != null && agenda.getQtdeProc() == null){
				descricao.append(" quantidade excluída: ").append(agendaOriginal.getQtdeProc());
			}
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarEspProcCirgs(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getEspProcCirgs(),agendaOriginal.getEspProcCirgs())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			MbcProcedimentoCirurgicos procOriginal = mbcProcedimentoCirurgicoDAO.obterPorChavePrimaria(agendaOriginal.getEspProcCirgs().getMbcProcedimentoCirurgicos().getSeq());
			MbcProcedimentoCirurgicos proc = mbcProcedimentoCirurgicoDAO.obterPorChavePrimaria(agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getSeq());
			
			descricao.append(" procedimento alterado de ").append(procOriginal.getDescricao());
			descricao.append(_PARA_).append(proc.getDescricao());
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarIndExclusao(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getIndExclusao(),agendaOriginal.getIndExclusao())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			if(!agendaOriginal.getIndExclusao() && agenda.getIndExclusao()){
				descricao.append(" agenda excluída");
			}
			if(agendaOriginal.getIndExclusao() && !agenda.getIndExclusao()){
				descricao.append(" exclusão de agenda suspensa");
			}
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarIndSituacao(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getIndSituacao(),agendaOriginal.getIndSituacao())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			descricao.append(" situação alterada de ").append(agendaOriginal.getIndSituacao().getDescricao()).
			append(_PARA_).append(agenda.getIndSituacao().getDescricao());
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarRegime(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getRegime(),agendaOriginal.getRegime())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			descricao.append(" regime alterado de ").append(agendaOriginal.getRegime().getDescricao()).
			append(_PARA_).append(agenda.getRegime().getDescricao());
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarEspecialidade(MbcAgendas agenda,
			MbcAgendas agendaOriginal, Boolean virgula, String virg,
			StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getEspecialidade(),agendaOriginal.getEspecialidade())){
			virgula = verificarVirgula(virgula, virg, descricao);
			
			descricao.append(" especialidade alterada de ").append(agendaOriginal.getEspecialidade().getNomeEspecialidade()).
			append(_PARA_).append(agenda.getEspecialidade().getNomeEspecialidade());
		}
		return virgula;
	}


	private Boolean getIncluirHistoricoVerificarUnidadeFuncional(
			MbcAgendas agenda, MbcAgendas agendaOriginal, Boolean virgula,
			String virg, StringBuilder descricao) {
		if(CoreUtil.modificados(agenda.getUnidadeFuncional(),agendaOriginal.getUnidadeFuncional())){
			virgula = verificarVirgula(virgula, virg, descricao);	
			
			if(agendaOriginal.getUnidadeFuncional() == null && agenda.getUnidadeFuncional() != null){
				descricao.append(" unidade incluída: ").append(agenda.getUnidadeFuncional().getDescricao());
			}			
			if(agendaOriginal.getUnidadeFuncional() != null && agenda.getUnidadeFuncional() != null){
				descricao.append(" unidade alterada de ").append(agendaOriginal.getUnidadeFuncional().getDescricao()).
				append(_PARA_).append(agenda.getUnidadeFuncional().getDescricao());
			}			
			if(agendaOriginal.getUnidadeFuncional() != null && agenda.getUnidadeFuncional() == null){
				descricao.append(" unidade excluida: ").append(agendaOriginal.getUnidadeFuncional().getDescricao());
			}
		}
		return virgula;
	}


	private Boolean verificarVirgula(Boolean virgula, String virg,
			StringBuilder descricao) {
		if(virgula){
			descricao.append(virg);
		}else{
			virgula = true;
		}
		return virgula;
	}

	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}
	
	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN(){
		return mbcAgendaHistoricoRN;
	}
	
	protected MbcAgendasRN getMbcAgendasRN() {
		return mbcAgendasRN;
	}
}