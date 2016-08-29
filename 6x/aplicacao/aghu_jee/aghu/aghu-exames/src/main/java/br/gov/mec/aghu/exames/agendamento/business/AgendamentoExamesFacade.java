package br.gov.mec.aghu.exames.agendamento.business;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioAgendaUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAgenda;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.EtiquetaEnvelopePacienteVO;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesGrupoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.GradeExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.GradeHorarioExtraVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorGradeVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorSalaVO;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorUnidadeVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelExaAgendPacVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExameUnidExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExamesDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioGradeExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoMarcacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.VAelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.AgendamentoExameRN;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAgendamentoMesmoHorarioVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGradeAgendaExameId;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExaAgendPac;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.model.VAelUnfExecutaExamesId;
import br.gov.mec.aghu.model.cups.ImpImpressora;

/**
 * Porta de entrada do sub módulo de agendamento do módulo exames.
 * 
 * @author diego.pacheco
 * 
 */
@SuppressWarnings({ "PMD.CouplingBetweenObjects" })
@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class AgendamentoExamesFacade extends BaseFacade implements IAgendamentoExamesFacade {


@EJB
private RelatorioAgendaPorGradeON relatorioAgendaPorGradeON;

@EJB
private RelatorioAgendasPorSalaON relatorioAgendasPorSalaON;

@EJB
private GradeAgendaExameON gradeAgendaExameON;

@EJB
private ConsultaHorarioLivreON consultaHorarioLivreON;

@EJB
private ExamesAgendamentoSelecaoON examesAgendamentoSelecaoON;

@EJB
private ConsultaHorarioLivreRN consultaHorarioLivreRN;

@EJB
private RelatorioAgendasPorUnidadeON relatorioAgendasPorUnidadeON;

@EJB
private GrupoExameRN grupoExameRN;

@EJB
private TipoMarcacaoExameRN tipoMarcacaoExameRN;

@EJB
private HorarioExameDispON horarioExameDispON;

@EJB
private GradeAgendaExameRN gradeAgendaExameRN;

@EJB
private HorarioGradeExameRN horarioGradeExameRN;

@EJB
private ItemHorarioAgendadoON itemHorarioAgendadoON;

@EJB
private GrupoExameON grupoExameON;

@EJB
private RelatorioEtiquetaEnvelopePacienteON relatorioEtiquetaEnvelopePacienteON;

@EJB
private ItemHorarioAgendadoRN itemHorarioAgendadoRN;

@EJB
private AgendamentoExameRN agendamentoExameRN;

@Inject
private AelGrupoExamesDAO aelGrupoExamesDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;

@Inject
private AelHorarioGradeExameDAO aelHorarioGradeExameDAO;

@Inject
private AelGrupoExameUnidExameDAO aelGrupoExameUnidExameDAO;

@Inject
private AelTipoMarcacaoExameDAO aelTipoMarcacaoExameDAO;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@Inject
private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;

@Inject
private VAelUnfExecutaExamesDAO vAelUnfExecutaExamesDAO;

@Inject
private AelHorarioExameDispDAO aelHorarioExameDispDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8004352621249027063L;
	
	@Override
	public AelItemSolicitacaoExames obterItemSolicitacaoExameOriginal(final Integer soeSeq, final Short seqp){
		AelItemSolicitacaoExames itemSolicitacaoExameOriginal = aelItemSolicitacaoExameDAO.obterItemSolicitacaoExamePorId(soeSeq, seqp);
		return itemSolicitacaoExameOriginal;
	}	
	
	@Override
	public List<Short> obterListaSeqUnFExamesAgendaveis(Integer soeSeq){
		return this.agendamentoExameRN.obterListaSeqUnFExamesAgendaveis(soeSeq);
	}
	
	@Override
	public String obterSugestaoAgendamentoPorPaciente(Integer pacCodigo, Boolean isAmbulatorio){
		return this.getConsultaHorarioLivreON().obterSugestaoAgendamentoPorPaciente(pacCodigo, isAmbulatorio);
	}
	
	@Override
	public String obterSugestaoAgendamentoPorPaciente(AipPacientes paciente,
			Boolean isAmbulatorio) {
		return this.getConsultaHorarioLivreON().obterSugestaoAgendamentoPorPaciente(paciente, isAmbulatorio);
	}
	
	@Override
	public List<Date> pesquisarSugestaoAgendamentoPorPaciente(Integer pacCodigo, Boolean isAmbulatorio){
		return this.getConsultaHorarioLivreON().pesquisarSugestaoAgendamentoPorPaciente(pacCodigo, isAmbulatorio);
	}
	
	@Override
	public List<ItemHorarioAgendadoVO> pesquisarAgendamentoPacientePorDatas(Integer pacCodigo, Date data1, Date data2){
		return this.getConsultaHorarioLivreON().pesquisarAgendamentoPacientePorDatas(pacCodigo, data1, data2);
	}
			
	
	@Override
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExameAtivoPorSeqOuDescricao(Object parametro) {
		return getAelTipoMarcacaoExameDAO().pesquisarTipoMarcacaoExameAtivoPorSeqOuDescricaoOrdenado(parametro);
	}
	
	@Override
	public List<AelHorarioExameDisp> pesquisarHorarioExameDisponibilidade(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, DominioSituacaoHorario filtroSituacao, Date filtroDtInicio, 
			Date filtroDtFim, Boolean filtroHorariosFuturos, DominioDiaSemana filtroDiaSemana, Date filtroHora, 
			AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq, 
			Integer gaeSeqp) {
		
		return getAelHorarioExameDispDAO().pesquisarHorarioExameDisponibilidade(firstResult, maxResult, orderProperty, asc, 
				filtroSituacao, filtroDtInicio, filtroDtFim, filtroHorariosFuturos, filtroDiaSemana, filtroHora, 
				filtroTipoMarcacao, filtroExtra, filtroExclusivo, gaeUnfSeq, gaeSeqp);
	}
	
	@Override
	public Long pesquisarHorarioExameDisponibilidadeCount(DominioSituacaoHorario filtroSituacao, Date filtroDtInicio, 
			Date filtroDtFim, Boolean filtroHorariosFuturos, DominioDiaSemana filtroDiaSemana, Date filtroHora, 
			AelTipoMarcacaoExame filtroTipoMarcacao, Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq, 
			Integer gaeSeqp) {
		
		return getAelHorarioExameDispDAO().pesquisarHorarioExameDisponibilidadeCount(filtroSituacao, filtroDtInicio, 
				filtroDtFim, filtroHorariosFuturos, filtroDiaSemana, filtroHora, filtroTipoMarcacao, filtroExtra, 
				filtroExclusivo, gaeUnfSeq, gaeSeqp);
	}
		
	@Override
	public List<GradeExameVO> pesquisarGradeExame(String orderProperty, boolean asc,
			Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, 
			RapServidores responsavel) {
		return getGradeAgendaExameON().pesquisarGradeExame(orderProperty, asc, seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
	}
	
	@Override
	public Long pesquisarGradeExameCount(
			Integer seq, AghUnidadesFuncionais unidadeExecutora, DominioSituacao situacao, 
			AelSalasExecutorasExames sala, AelGrupoExames grupoExame, VAelUnfExecutaExames exame, 
			RapServidores responsavel) {
		return getAelGradeAgendaExameDAO().pesquisarGradeExameCount(seq, unidadeExecutora, situacao, sala, grupoExame, exame, responsavel);
	}
	
	@Override
	public AelHorarioExameDisp refreshHorarioExameDisp(AelHorarioExameDisp horarioExameDisp) {
		return getHorarioExameDispON().refreshHorarioExameDisp(horarioExameDisp);
	}

	@Override
	public void refreshTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExame) {
		getTipoMarcacaoExameRN().refreshTipoMarcacaoExame(tipoMarcacaoExame);
	}
	
	@Override
	public List<EtiquetaEnvelopePacienteVO> pesquisarEtiquetaEnvelopePaciente(Integer codSolicitacao, Short unfSeq) {
		return getRelatorioEtiquetaEnvelopePacienteON().pesquisarEtiquetaEnvelopePaciente(codSolicitacao, unfSeq);
	}
	
	@Override
	public AelGradeAgendaExame obterGradeExamePorChavePrimaria(AelGradeAgendaExameId id) {
		AelGradeAgendaExame retorno;
		
		
		retorno = getAelGradeAgendaExameDAO().obterPorChavePrimariaFull(id);
				 		 
		return retorno;
	}
	
	@Override
	public Long obterCountHorarioExameDispTipoMarcacaoAtivaPorGrade(Short unfSeq, Integer seqp) {
		return getAelHorarioExameDispDAO().obterCountHorarioExameDispTipoMarcacaoAtivaPorGrade(unfSeq, seqp);
	}

	@Override
	public Long obterCountHorarioGradeExameGrade(Short unfSeq, Integer seqp) {
		return getAelHorarioGradeExameDAO().contarAelHorarioGradeExameByAelGradeAgendaExame(unfSeq, seqp);
	}
	
	@Override
	public Boolean verificarSituacaoHorarioIndisponivel(AelHorarioExameDisp horarioExameDisp) {
		return getHorarioExameDispON().verificarSituacaoHorarioIndisponivel(horarioExameDisp);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterDisponibilidadeHorarios','manterDisponibilidade')}")
	public void atualizarListaHorarioExameDisp(List<AelHorarioExameDisp> listaHorarioExameDisp, 
			DominioSituacaoHorario situacaoHorario, AelTipoMarcacaoExame tipoMarcacaoExame, Boolean extra, Boolean exclusivo) 
				throws BaseException {
		getHorarioExameDispON().atualizarListaHorarioExameDisp(listaHorarioExameDisp, situacaoHorario, tipoMarcacaoExame,
				extra, exclusivo);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterDisponibilidadeHorarios','manterDisponibilidade')}")
	public void inserirHorarioExameDisp(DominioSituacaoHorario situacaoHorario,
			AelTipoMarcacaoExame tipoMarcacaoExame, Boolean extra, Boolean exclusivo, Short gaeUnfSeq, Integer gaeSeqp,
			Date dthrAgenda) throws BaseException {
		getHorarioExameDispON().inserirHorarioExameDisp(situacaoHorario, tipoMarcacaoExame, extra, exclusivo, 
				gaeUnfSeq, gaeSeqp, dthrAgenda);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterDisponibilidadeHorarios','manterDisponibilidade')}")
	public Boolean removerListaHorarioExameDisp(List<AelHorarioExameDisp> listaHorarioExame) 
	throws ApplicationBusinessException {
		return getHorarioExameDispON().removerListaHorarioExameDisp(listaHorarioExame);
	}
	
	@Override
	@Secure("#{s:hasPermission('agendarExame','visualizarHorariosAgendamento')}")
	public List<VAelHrGradeDispVO> pesquisarHorariosLivresParaExame(String sigla, Integer matExame, Short unfExame, Date dataReativacao, Integer soeSeq, Short seqp, Date data, Date hora, Integer grade, AelGrupoExames grupoExame, AelSalasExecutorasExames salaExecutoraExame, RapServidores servidor) throws ApplicationBusinessException, ParseException {
		return this.getConsultaHorarioLivreON().pesquisarHorariosLivresParaExame(sigla, matExame, unfExame, dataReativacao, soeSeq, seqp, data, hora, grade, grupoExame, salaExecutoraExame, servidor);
	}
	
	@Override
	public Long obterExamesAgendamentosPaciente(Integer pacCodigo, AelUnfExecutaExamesId unfExecutaExamesId) {
		return this.getAelItemHorarioAgendadoDAO().obterExamesAgendamentosPaciente(pacCodigo, unfExecutaExamesId);	}
	
	@Override
	@Secure("#{s:hasPermission('agendarExame','visualizarExamesAgendadosPaciente')}")
	public List<VAelExaAgendPacVO> obterExamesAgendadosDoPaciente(Integer pacCodigo, String siglaExame, Integer matSeqExame, Short unfSeqExame) {
		return this.getConsultaHorarioLivreON().obterExamesAgendadosDoPaciente(pacCodigo, siglaExame, matSeqExame, unfSeqExame);
	}
	
	@Override
	public List<VAelExaAgendPac> obterInformacoesExamesAgendadosPaciente(Integer pacCodigo, String siglaExame, Integer matSeqExame, 
			Short unfSeqExame, Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		return this.getConsultaHorarioLivreON().obterInformacoesExamesAgendadosPaciente(pacCodigo, siglaExame, matSeqExame, unfSeqExame, hedGaeUnfSeq,
				hedGaeSeqp, hedDthrAgenda);
	}

	@Override
	public AelHorarioExameDisp obterHorarioExameDisp(Date dthrAgenda, Short gaeUnfSeq, Integer gaeSeqp) {
		return getAelHorarioExameDispDAO().obterPorId(gaeUnfSeq, gaeSeqp, dthrAgenda);
	}

    @Override
    public AelHorarioExameDisp obterHorarioExameDisponivel(Date dthrAgenda, Short gaeUnfSeq, Integer gaeSeqp) {
        return getAelHorarioExameDispDAO().obterHorarioExameDisponivel(gaeUnfSeq, gaeSeqp, dthrAgenda);
    }

    protected AelTipoMarcacaoExameDAO getAelTipoMarcacaoExameDAO() {
		return aelTipoMarcacaoExameDAO;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO() {
		return aelGradeAgendaExameDAO;
	}
	
	protected HorarioExameDispON getHorarioExameDispON() {
		return horarioExameDispON;
	}
	
	@Override
	public List<AelHorarioGradeExame> pesquisaHorariosPorGrade (AelGradeAgendaExame grade){
		return getAelHorarioGradeExameDAO().pesquisaPorGrade(grade);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGradeAgendamento','manterGrade')}")
	public void excluirHorarioGrade(AelHorarioGradeExame horarioGrade){
		AelHorarioGradeExame newHorarioGrade = getAelHorarioGradeExameDAO().obterPorChavePrimaria(horarioGrade.getId()); 
		getAelHorarioGradeExameDAO().remover(newHorarioGrade);
		getAelHorarioGradeExameDAO().flush();
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGradeAgendamento','manterGrade')}")
	public void inserirHorarioGradeExame(AelHorarioGradeExame horarioGrade) throws ApplicationBusinessException{
		getHorarioGradeExameRN().inserirHorarioGradeExame(horarioGrade);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGradeAgendamento','manterGrade')}")
	public void atualizarHorarioGradeExame(AelHorarioGradeExame horarioGrade) throws ApplicationBusinessException{
		getHorarioGradeExameRN().atualizarHorarioGradeExame(horarioGrade);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGradeAgendamento','manterGrade')}")
	public void removerGradeAgendaExame(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		getGradeAgendaExameON().removerGradeAgendaExame(grade);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGradeAgendamento','manterGrade')}")
	public void persistirGradeAgendaExame(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		getGradeAgendaExameON().persistirGradeAgendaExame(grade);
	}
	
	@Override
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Short tipoMarcacaoExameSeq, String tipoMarcacaoExameDescricao, 
			DominioSituacao tipoMarcacaoExameSituacao) {
		return getAelTipoMarcacaoExameDAO().pesquisarTipoMarcacaoExame(firstResult, maxResults, orderProperty, asc, 
				tipoMarcacaoExameSeq, tipoMarcacaoExameDescricao, tipoMarcacaoExameSituacao);
	}
	
	@Override
	public Long pesquisarTipoMarcacaoExameCount(Short tipoMarcacaoExameSeq, String tipoMarcacaoExameDescricao, 
			DominioSituacao tipoMarcacaoExameSituacao) {
		return getAelTipoMarcacaoExameDAO().pesquisarTipoMarcacaoExameCount(tipoMarcacaoExameSeq, 
				tipoMarcacaoExameDescricao, tipoMarcacaoExameSituacao);
	}	
	
	@Override
	@Secure("#{s:hasPermission('manterTipoMarcacao','persistir')}")
	public void excluirTipoMarcacaoExame(Short seq) throws ApplicationBusinessException {
		getTipoMarcacaoExameRN().excluirTipoMarcacaoExame(seq);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterTipoMarcacao','persistir')}")
	public void persistirTipoMarcacaoExame(AelTipoMarcacaoExame tipoMarcacaoExameNew) throws ApplicationBusinessException {
		getTipoMarcacaoExameRN().persistirTipoMarcacaoExame(tipoMarcacaoExameNew);
	}
	
	@Override
	public AelTipoMarcacaoExame obterTipoMarcacaoExamePorSeq(Short seq) {
		return getAelTipoMarcacaoExameDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public void verificarDelecaoTipoMarcacaoExame(Date dataCriadoEm) throws ApplicationBusinessException {
		getTipoMarcacaoExameRN().verificarDelecao(dataCriadoEm);
	}
	
	@Override
	public List<AelHorarioExameDisp> pesquisarHorarioExameDisponibilidadeExcetoMarcadoExecutado(
			DominioSituacaoHorario filtroSituacao, Date filtroDtInicio, Date filtroDtFim, Boolean filtroHorariosFuturos, 
			DominioDiaSemana filtroDiaSemana, Date filtroHora, AelTipoMarcacaoExame filtroTipoMarcacao, 
			Boolean filtroExtra, Boolean filtroExclusivo, Short gaeUnfSeq, Integer gaeSeqp) {
		
		return getAelHorarioExameDispDAO().pesquisarHorarioExameDisponibilidadeExcetoMarcadoExecutado(filtroSituacao, 
				filtroDtInicio, filtroDtFim, filtroHorariosFuturos, filtroDiaSemana, filtroHora, filtroTipoMarcacao, 
				filtroExtra, filtroExclusivo, gaeUnfSeq, gaeSeqp);
	}
	
	@Override
	public List<RelatorioAgendaPorSalaVO> obterAgendasPorSala(AghUnidadesFuncionais unidadeExecutora, Date dtAgenda,
			AelSalasExecutorasExames sala, Boolean impHorariosLivres, Boolean impEtiquetas, Boolean impTickets) {
		
		return getRelatorioAgendasPorSalaON().obterAgendasPorSala(unidadeExecutora, dtAgenda, sala, impHorariosLivres, impEtiquetas, impTickets);
	}
	
	@Override
	@Secure("#{s:hasPermission('gerarDisponibilidadeHorarios','gerarDisponibilidade')}")
	public Integer gerarDisponibilidadeHorarios(AelGradeAgendaExame grade, Date dataInicio, Date dataFim, Date dataHoraUltimaGrade) throws BaseException{
		return getGradeAgendaExameON().gerarDisponibilidadeHorarios(grade, dataInicio, dataFim, dataHoraUltimaGrade);
	}

	@Override
	public VAelUnfExecutaExames obterVAelUnfExecutaExamesPorId(VAelUnfExecutaExamesId id){
		return getVAelUnfExecutaExamesDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public AelUnfExecutaExames obterAelUnfExecutaExamesDAOPorId(AelUnfExecutaExamesId id){
		return getAelUnfExecutaExamesDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public List<Integer> obterSolicitacoesExame(List<RelatorioAgendaPorSalaVO> colecao) {
		return getRelatorioAgendasPorSalaON().obterSolicitacoesExame(colecao);
	}
	
	@Override
	public List<AelSolicitacaoExames> obterSolicitacoesExamePorSeq(List<RelatorioAgendaPorSalaVO> colecao) {
		return getRelatorioAgendasPorSalaON().obterSolicitacoesExamePorSeq(colecao);
	}
	
	@Override
	public void validarExamesAgendamentoSelecao(VAelSolicAtendsVO exameVO, AipPacientes paciente) throws ApplicationBusinessException {
		getExamesAgendamentoSelecaoON().validarExamesAgendamentoSelecao(exameVO, paciente);
	}
	
	@Override
	public VAelSolicAtendsVO obterVAelSolicAtendsPorSoeSeq(Integer soeSeq) throws ApplicationBusinessException {
		return getExamesAgendamentoSelecaoON().obterVAelSolicAtendsPorSoeSeq(soeSeq);
	}
	
	@Override
	public List<AgendamentoExameVO> permiteAgendarExames(List<AgendamentoExameVO> exames, final String login,String operacao) throws ApplicationBusinessException {
		 return getExamesAgendamentoSelecaoON().permiteAgendarExames(exames, login, operacao);
	}
	
	@Override
	public List<AelGrupoExames> pesquisarGrupoExame(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, AelGrupoExames grupoExame) {
		return getAelGrupoExamesDAO().pesquisarGrupoExame(firstResult, maxResults, orderProperty, asc, 
				grupoExame);
	}
	
	@Override
	public Long pesquisarGrupoExameCount(AelGrupoExames grupoExame) {
		return getAelGrupoExamesDAO().pesquisarGardeExameCount(grupoExame);
	}
	
	@Override
	public AelGrupoExames obterAelGrupoExamePeloId(final Integer seq) {
		return this.getAelGrupoExamesDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AelGrupoExames obterAelGrupoExamePeloId(final Integer seq, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getAelGrupoExamesDAO().obterPorChavePrimaria(seq, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGrupoExameAgendamento','persistir')}")
	public void excluirGrupoExame(Integer seq) throws ApplicationBusinessException {
		final AelGrupoExames grupoExame = getAelGrupoExamesDAO().obterPorChavePrimaria(seq);
		
		if (grupoExame == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		getGrupoExameON().validarGrupoExameUtilizadoGradeAgendaExame(grupoExame);
		getGrupoExameRN().executarBeforeDeleteGrupoExame(grupoExame.getCriadoEm());
		List<AelGrupoExameUnidExame> listGexUnid = getAelGrupoExameUnidExameDAO().pesquisarGrupoExameUnidExamePorGexSeq(grupoExame.getSeq());
		for(AelGrupoExameUnidExame unid : listGexUnid) {
			getAelGrupoExameUnidExameDAO().remover(unid);
		}
		getAelGrupoExamesDAO().remover(grupoExame);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGrupoExameAgendamento','persistir')}")
	public void inserirGrupoExame(AelGrupoExames grupoExame) throws ApplicationBusinessException {
		getGrupoExameRN().executarBeforeInsertGrupoExame(grupoExame);
		getAelGrupoExamesDAO().persistir(grupoExame);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterGrupoExameAgendamento','persistir')}")
	public void alterarGrupoExame(AelGrupoExames grupoExame) throws ApplicationBusinessException {
		AelGrupoExames aelGrupoExameOld = getAelGrupoExamesDAO().obterOriginal(grupoExame.getSeq());
		getGrupoExameRN().executarBeforeUpdateGrupoExame(grupoExame, aelGrupoExameOld);
		getAelGrupoExamesDAO().merge(grupoExame);
	}
	
	@Override
	public List<ExamesGrupoExameVO> buscarListaExamesGrupoExameVOPorCodigoGrupoExame(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigoGrupoExame) {
		return getAelGrupoExameUnidExameDAO().buscarListaExamesGrupoExameVOPorCodigoGrupoExame(firstResult, maxResult, orderProperty, asc, codigoGrupoExame);
	}

	@Override
	public Long countBuscarListaExamesGrupoExameVOPorCodigoGrupoExame(Integer codigo) {
		return getAelGrupoExameUnidExameDAO().countBuscarListaExamesGrupoExameVOPorCodigoGrupoExame(codigo);
	}
	
	@Override
	public void inserirGrupoExameUnidExame(AelGrupoExameUnidExame grupoExameUnidExame) throws ApplicationBusinessException {
		getGrupoExameON().validarExameExistenteEmGrupoExame(grupoExameUnidExame.getId());
		getGrupoExameRN().executarBeforeInsertGrupoExameUnidExame(grupoExameUnidExame);
		getAelGrupoExameUnidExameDAO().persistir(grupoExameUnidExame);
	}
	
	@Override
	public void excluirGrupoExameUnidExame(AelGrupoExameUnidExameId id) throws ApplicationBusinessException {
		getAelGrupoExameUnidExameDAO().removerPorId(id);
	}
	
	@Override
	public void alterarGrupoExameUnidExame(AelGrupoExameUnidExame grupoExameUnidExame) throws ApplicationBusinessException {
		getGrupoExameRN().executarBeforeUpdateGrupoExameUnidExame(grupoExameUnidExame);
		getAelGrupoExameUnidExameDAO().merge(grupoExameUnidExame);
	}
	
	@Override
	public AelGrupoExameUnidExame obterGrupoExameUnidExamePorId(AelGrupoExameUnidExameId id) {
		return getAelGrupoExameUnidExameDAO().obterPorChavePrimariaFull(id);
	}
	
	@Override
	public List<AgendamentoExameVO> obterExamesParaAgendamento(VAelSolicAtendsVO filtro, Short unidadeExecutoraSeq) throws ApplicationBusinessException {
		return getExamesAgendamentoSelecaoON().obterExamesParaAgendamento(filtro, unidadeExecutoraSeq);
	}
	@Override
	public List<AgendamentoExameVO> verificarExamesNaoSelecComMesmaAmostra(List<AgendamentoExameVO> listaItensExame, 
			AghParametros parametro, String label) throws ApplicationBusinessException {
		return getExamesAgendamentoSelecaoON().verificarExamesNaoSelecComMesmaAmostra(listaItensExame, parametro, label);
	}
	
	//TODO: DiegoPacheco --> retirar o trecho de código abaixo 
	// qdo a estória #5494 que chama esta estória estiver implementada
	@Override
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExameAtendimentoPacientePorSoeSeqSeqp(
			final Integer soeSeq, final List<Short> listaSeqp) {
		return getAelItemSolicitacaoExameDAO()
				.pesquisarItemSolicitacaoExameAtendimentoPacientePorSoeSeqSeqp(soeSeq, listaSeqp);
	}
	
	@Override
	@Secure("#{s:hasPermission('agendarExame','cancelar')}")
	public void cancelarItemHorarioAgendadoMarcado(AelItemHorarioAgendadoId itemHorarioAgendadoId, Short globalUnfSeq, String nomeMicrocomputador)	throws BaseException {
		getItemHorarioAgendadoON().cancelarItemHorarioAgendadoMarcado(itemHorarioAgendadoId, globalUnfSeq, nomeMicrocomputador);
	}
	
	@Override
	@Secure("#{s:hasPermission('agendarExame','cancelar')}")
	public void cancelarItemHorarioAgendadoMarcadoPorSelecaoExames(List<AgendamentoExameVO> examesAgendamentoSelecao, 
			Short globalUnfSeq, String nomeMicrocomputador) throws BaseException {
		getItemHorarioAgendadoON().cancelarItemHorarioAgendadoMarcadoPorSelecaoExames(
				examesAgendamentoSelecao, globalUnfSeq, nomeMicrocomputador);
	}

	@Override
	public List<VAelSolicAtendsVO> obterSolicitacoesExame(VAelSolicAtendsVO filtro) throws ApplicationBusinessException {
		return getExamesAgendamentoSelecaoON().obterSolicitacoesExame(filtro);
	}
	
	@Override	
	public List<AgendamentoExameVO> obterExamesSelecionados(List<AgendamentoExameVO> exames) {
		return getExamesAgendamentoSelecaoON().obterExamesSelecionados(exames);
	}
	
	@Override
	public void verificaImpressaoTicketExame(List<AgendamentoExameVO> examesAgendamentoSelecao) throws ApplicationBusinessException {
		this.getExamesAgendamentoSelecaoON().verificaImpressaoTicketExame(examesAgendamentoSelecao);
	}
	
	
	@Override
	public void inserirItemHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, Boolean permiteHoraExtra, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String sigla, Integer materialAnalise, Short unfExecutora, String nomeMicrocomputador) 
					throws BaseException {
		this.getItemHorarioAgendadoON().inserir(itemHorarioAgendado, itemSolicitacaoExameOriginal, permiteHoraExtra, agendaExameMesmoHorario, agendamentoSingular, 
				sigla, materialAnalise, unfExecutora, nomeMicrocomputador);
	}
	
	@Override
	public void inserirItemHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado, String nomeMicrocomputador) throws BaseException{
		this.getItemHorarioAgendadoRN().inserirItemHorarioAgendado(itemHorarioAgendado, null, nomeMicrocomputador);
	}
	
	@Override
	public AelItemHorarioAgendado obterItemHorarioAgendadoPorId(AelItemHorarioAgendadoId itemHorarioAgendadoId) {
		return this.getAelItemHorarioAgendadoDAO().obterPorChavePrimaria(itemHorarioAgendadoId);
	}
	
	@Override
	public List<GradeHorarioExtraVO> pesquisarGradeHorarioExtra(Object parametro, Short grade, String sigla, Integer matExame, Short unfExame){
		return this.getConsultaHorarioLivreON().pesquisarGradeExame(parametro, grade, sigla, matExame, unfExame);
	}
	
	@Override
	public Date obterDataHoraDisponivelParaGradeEUnidadeExecutora(Short unfExecutora, Integer grade, Date dataHora){
		return this.getConsultaHorarioLivreON().obterDataHoraDisponivelParaGradeEUnidadeExecutora(unfExecutora, grade, dataHora);
	}
	
	@Override
	public Short obterTipoMarcacao(DominioOrigemAtendimento origem, Short unfSeq) throws ApplicationBusinessException{
		return this.getConsultaHorarioLivreRN().obterTipoMarcacao(origem, unfSeq);
	}

	@Override
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGaeUnfSeqGaeSeqpDthrAgenda(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Date hedDthrAgenda) {
		return getAelItemHorarioAgendadoDAO().pesquisarItemHorarioAgendadoPorGaeUnfSeqGaeSeqpDthrAgenda(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
	}
	
	@Override
	public List<RelatorioAgendaPorUnidadeVO> obterAgendasPorUnidade(AghUnidadesFuncionais unidadeExecutora, Date dtInicio,
			Date dtFim, DominioOrigemPacienteAgenda origem, DominioOrdenacaoRelatorioAgendaUnidade ordenacao) throws ApplicationBusinessException {
		return getRelatorioAgendasPorUnidadeON().obterAgendasPorUnidade(unidadeExecutora, dtInicio, dtFim, origem, ordenacao);
	}
	
	@Override
	public List<Integer> obterSolicitacoesExameUnidade(List<RelatorioAgendaPorUnidadeVO> colecao) {
		return getRelatorioAgendasPorUnidadeON().obterSolicitacoesExame(colecao);
	}
	
	@Override
	public void verificarHorarioEscolhido(AelItemHorarioAgendado itemHorarioAgendado, Short seqp, Boolean permiteHoraExtra, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, AelUnfExecutaExames unfExecutoraExame, String nomeMicrocomputador) throws BaseException {
		getItemHorarioAgendadoRN().verificarHorarioEscolhido(itemHorarioAgendado, seqp, permiteHoraExtra, agendaExameMesmoHorario, 
				agendamentoSingular, unfExecutoraExame, nomeMicrocomputador);
	}
	
	@Override
	public void identificarAgendamentoExamesEmGrupo(Integer grupoExameSeq, List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			Boolean horarioExtra) {
		getItemHorarioAgendadoON().identificarAgendamentoExamesEmGrupo(grupoExameSeq, listaItemHorarioAgendadoVO, horarioExtra);
	}
	
	@Override
	public Boolean validarAgendamentoExamesEmGrupo(List<AgendamentoExameVO> agendamentosExameVO, Integer grupoExameSeq, Short idSelecionado) {
		return getItemHorarioAgendadoON().validarAgendamentoExamesEmGrupo(agendamentosExameVO, grupoExameSeq,idSelecionado);
	}
	
	@Override
	@Secure("#{s:hasPermission('agendarExame','gerarHorarioExtra')}")
	public Date gravarHorarioExtra(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, GradeHorarioExtraVO gradeHorarioExtraVO, 
			Short unfExecutora, Date dataHora, AelTipoMarcacaoExame tipoMarcacaoExame, String nomeMicrocomputador) throws BaseException {
		return getItemHorarioAgendadoON().gravarHorarioExtra(listaItemHorarioAgendadoVO, gradeHorarioExtraVO, 
				unfExecutora, dataHora, tipoMarcacaoExame, nomeMicrocomputador);
	}
	
	@Override
	public List<AelItemHorarioAgendado> pesquisarItemHorarioAgendadoPorGradeESoeSeq(Short hedGaeUnfSeq, Integer hedGaeSeqp, 
			Integer soeSeq) {
		return getAelItemHorarioAgendadoDAO().pesquisarItemHorarioAgendadoPorGradeESoeSeq(hedGaeUnfSeq, hedGaeSeqp, soeSeq);
	}
	
	@Override
	public void inserirItemHorarioAgendado(ItemHorarioAgendadoVO itemHorarioAgendadoVO, VAelHrGradeDispVO vAelHrGradeDispVO, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String nomeMicrocomputador) throws BaseException {
		getItemHorarioAgendadoON().inserirItemHorarioAgendado(itemHorarioAgendadoVO, vAelHrGradeDispVO, agendaExameMesmoHorario,
				agendamentoSingular, nomeMicrocomputador, itemHorarioAgendadoVO.getItemSolicitacaoExameOriginal());
	}
	
	@Override
	public void inserirItemHorarioAgendadoExtra(ItemHorarioAgendadoVO itemHorarioAgendadoVO, AelHorarioExameDisp horarioExameDisp, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String nomeMicrocomputador) throws BaseException {
		getItemHorarioAgendadoON().inserirItemHorarioAgendadoExtra(itemHorarioAgendadoVO, horarioExameDisp, agendaExameMesmoHorario, 
				agendamentoSingular, nomeMicrocomputador);
	}
	
	@Override
	public AelHorarioExameDisp montarHorarioExameDisp(Date dataHora, Short unfExecutora, Integer seqGrade, 
			AelTipoMarcacaoExame tipoMarcacaoExame) throws ApplicationBusinessException {
		return getHorarioExameDispON().montarHorarioExameDisp(dataHora, unfExecutora, seqGrade, tipoMarcacaoExame);
	}
	
	
	@Override
	public void desfazerIdentificacaoAgendamentoExamesEmGrupo(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		getItemHorarioAgendadoON().desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
	}
	
	@Override
	public List<ItemHorarioAgendadoVO> obterListaExamesParaAgendamentoEmGrupo(Integer soeSeq, List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, Short hedGaeUnfSeq,
			Integer hedGaeSeqp, Short seqp) throws ApplicationBusinessException {
		return getConsultaHorarioLivreON().obterListaExamesParaAgendamentoEmGrupo(soeSeq, listaItemHorarioAgendadoVO, hedGaeUnfSeq, hedGaeSeqp, seqp);
	}
	
	@Override
	public List<ItemHorarioAgendadoVO> atualizarListaItemHorarioAgendadoVO( List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, 
			Short unfGrade, Integer gradeSeqp, String sala, Integer soeSeq) {
		return getConsultaHorarioLivreON().atualizarListaItemHorarioAgendadoVO( listaItemHorarioAgendadoVO, unfGrade, gradeSeqp, sala, soeSeq);
	}
	
	@Override
	public String gerarArquivoAgendas(List<RelatorioAgendaPorGradeVO> listaRelatorioAgendaPorGradeVO) throws IOException {
		return this.getRelatorioAgendaPorGradeON().gerarArquivoAgendas(listaRelatorioAgendaPorGradeVO);
	}
	
	@Override
	public List<RelatorioAgendaPorGradeVO> obterAgendasPorGrade(Short gaeUnfSeq, Integer gaeSeqp,
			Date dthrAgendaInicial, Date dthrAgendaFinal, Boolean impHorariosLivres, Boolean isPdf) {
		return this.getRelatorioAgendaPorGradeON()
			.obterAgendasPorGrade(gaeUnfSeq, gaeSeqp, dthrAgendaInicial, dthrAgendaFinal, impHorariosLivres, isPdf);
	}
	
	@Override
	public List<AelGradeAgendaExame> pesquisarGradeAgendaExamePorSeqpUnfSeq(Object parametro, Short unfSeq) {
		return getAelGradeAgendaExameDAO().pesquisarGradeAgendaExamePorSeqpUnfSeq(parametro, unfSeq);
	}
	
	@Override
	public List<AelGradeAgendaExame> pesquisarGradeExamePorUnidadeExec(Object parametro, Short unfSeq) {
		return getAelGradeAgendaExameDAO().pesquisarGradeExamePorUnidadeExec(parametro, unfSeq);
	}
	
	@Override
	public void validarFiltrosRelatorioAgendas(Date dataInicial, Date dataFinal, AelGradeAgendaExame gradeAgendaExame)
			throws ApplicationBusinessException {
		getRelatorioAgendaPorGradeON().validarFiltrosRelatorioAgendas(dataInicial, dataFinal, gradeAgendaExame);
	}

	@Override
	public void validarPeriodoRelatorioAgendas(Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException {
		getRelatorioAgendaPorGradeON().validarPeriodoRelatorioAgendas(dataInicial, dataFinal);
	}

	@Override
	public List<Integer> obterListaSoeSeqGradeAgenda(List<RelatorioAgendaPorGradeVO> listaRelatorioAgendaPorGradeVO) {
		return getRelatorioAgendaPorGradeON().obterListaSoeSeqGradeAgenda(listaRelatorioAgendaPorGradeVO);
	}
	
	protected TipoMarcacaoExameRN getTipoMarcacaoExameRN() {
		return tipoMarcacaoExameRN;
	}
	
	protected ItemHorarioAgendadoRN getItemHorarioAgendadoRN() {
		return itemHorarioAgendadoRN;
	}
	
	protected AelHorarioGradeExameDAO getAelHorarioGradeExameDAO(){
		return aelHorarioGradeExameDAO;
	}
	
	protected HorarioGradeExameRN getHorarioGradeExameRN(){
		return horarioGradeExameRN;
	}
	
	protected GradeAgendaExameON getGradeAgendaExameON(){
		return gradeAgendaExameON;
	}
	
	protected RelatorioEtiquetaEnvelopePacienteON getRelatorioEtiquetaEnvelopePacienteON(){
		return relatorioEtiquetaEnvelopePacienteON;
	}
	
	protected RelatorioAgendasPorSalaON getRelatorioAgendasPorSalaON(){
		return relatorioAgendasPorSalaON;
	}
	
	protected ConsultaHorarioLivreON getConsultaHorarioLivreON(){
		return consultaHorarioLivreON;
	}
	
	protected ConsultaHorarioLivreRN getConsultaHorarioLivreRN() {
		return consultaHorarioLivreRN;
	}
	
	protected VAelUnfExecutaExamesDAO getVAelUnfExecutaExamesDAO(){
		return vAelUnfExecutaExamesDAO;
	}
	
	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO(){
		return aelUnfExecutaExamesDAO;
	}
	
	protected GradeAgendaExameRN getGradeAgendaExameRN(){
		return gradeAgendaExameRN;
	}
	
	protected ExamesAgendamentoSelecaoON getExamesAgendamentoSelecaoON(){
		return examesAgendamentoSelecaoON;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}

	protected ItemHorarioAgendadoON getItemHorarioAgendadoON() {
		return itemHorarioAgendadoON;
	}
	
	protected AelGrupoExamesDAO getAelGrupoExamesDAO() {
		return aelGrupoExamesDAO;
	}
	
	protected GrupoExameON getGrupoExameON() {
		return grupoExameON;
	}
	
	protected GrupoExameRN getGrupoExameRN() {
		return grupoExameRN;
	}
	
	protected AelGrupoExameUnidExameDAO getAelGrupoExameUnidExameDAO() {
		return aelGrupoExameUnidExameDAO;
	}
	
	protected RelatorioAgendasPorUnidadeON getRelatorioAgendasPorUnidadeON() {
		return relatorioAgendasPorUnidadeON;
	}
	
	protected RelatorioAgendaPorGradeON getRelatorioAgendaPorGradeON() {
		return relatorioAgendaPorGradeON;
	}

	@Override
	public Boolean isImprimeTicketsAgendas(AghUnidadesFuncionais unidadeExecutora, Boolean impTickets) {
		return getRelatorioAgendasPorSalaON().isImprimeTicketsAgendas(unidadeExecutora, impTickets);
	}

	@Override
	public ImpImpressora isImprimeEtiquetasAgendas(AghUnidadesFuncionais unidadeExecutora, Boolean impEtiquetas) throws BaseException {
		return getRelatorioAgendasPorSalaON().isImprimeEtiquetasAgendas(unidadeExecutora, impEtiquetas);
	}

    @Override
    public boolean validaHabilitaAgendamento(Integer atendimentoSeq, Integer solicitacaoExamesSeq,
                                             Integer seqAtendimentoDiverso, Short unidadeFuncionalSolicitanteSeq, Short unidadeFuncionalUsuarioSeq) {
        return agendamentoExameRN.validaAgendamentoExame(atendimentoSeq, solicitacaoExamesSeq,
                seqAtendimentoDiverso, unidadeFuncionalSolicitanteSeq, unidadeFuncionalUsuarioSeq);
    }
    
	@Override
	public Date obterHorarioExameDataMaisRecentePorGrade(Short unidFuncSeq, Integer gaeSeqp) {
		
		return getAelHorarioExameDispDAO().obterHorarioExameDataMaisRecentePorGrade(unidFuncSeq, gaeSeqp) ;
	}
 

    @Override
    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosAgendadosMesmoGrupoExames(AghAtendimentos atendimento,
                                                                                          AelItemSolicitacaoExames itemSolicitacaoExames) {
        return agendamentoExameRN.pesquisaHorariosAgendamentoMesmoGrupoExames(atendimento, itemSolicitacaoExames);
    }

    @Override
    public List<ExameAgendamentoMesmoHorarioVO> pesquisaHorariosDisponiveisAgendamentoConcorrente(Integer codigoPaciente,
                                                                                        String siglaExame,
                                                                                        Integer seqMaterial) {
        return agendamentoExameRN.pesquisaHorariosDisponiveisAgendamentoConcorrente(codigoPaciente, siglaExame, seqMaterial);
    }

    @Override
    public List<AelItemSolicitacaoExames> buscarItensSolicitacaoExameNaoAgendados(final Integer soeSeq, final List<Short> listaSeqUnF){
    	return examesAgendamentoSelecaoON.buscarItensSolicitacaoExameNaoAgendados(soeSeq, listaSeqUnF);
    }
    
    @Override
    public boolean verificarExistenciaItensSolicitacaoExameNaoAgendados(final Integer soeSeq){
    	return examesAgendamentoSelecaoON.verificarExistenciaItensSolicitacaoExameNaoAgendados(soeSeq);
    }
    
    @Override
    public boolean verificarExistenciaAmostrasComum(List<AgendamentoExameVO> listaItensExame){
        return examesAgendamentoSelecaoON.verificarExistenciaAmostrasComum(listaItensExame);
    }
}
