package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.AgendamentosExcluidosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasAgendaMedicoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ListaEsperaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasAgendaEscalaDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasC2VO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasParametrosVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.AgendaCirurgiaSolicitacaoEspecialVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendaAnotacaoId;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.VMbcProcEsp;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IBlocoCirurgicoPortalPlanejamentoFacade extends Serializable {

	public MbcAgendaAnotacao obterMbcAgendaAnotacaoPorChavePrimaria(MbcAgendaAnotacaoId mbcAgendaAnotacaoId);

	List<RelatorioEscalaDeSalasVO> listarEquipeSalas(Short seqUnidade);

	public Collection<RelatorioPortalPlanejamentoCirurgiasVO> listarEquipePlanejamentoCirurgias(
			Date pDtIni, Date pDtFim, Integer pPucSerMatricula,
			Short pPucSerVinCodigo, Short pPucUnfSeq, DominioFuncaoProfissional pPucIndFuncaoProf,
			Short pEspSeq, Short pUnfSeq, String pEquipe) throws ApplicationBusinessException;

	public List<ListaEsperaVO> listaEsperaRecuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PortalPesquisaCirurgiasParametrosVO parametros) throws ApplicationBusinessException;
	
	public Long listaEsperaRecuperarCount(PortalPesquisaCirurgiasParametrosVO parametros);

	public List<AghCaractUnidFuncionais> listarAghCaractUnidFuncionais(String objPesquisa);

	public List<PortalPesquisaCirurgiasC2VO> listarMbcProfAtuaUnidCirgsPorUnfSeq(Short unfSeq,
			String strPesquisa);

	public Long listarMbcProfAtuaUnidCirgsPorUnfSeqCount(Short unfSeq,
			String strPesquisa);

	public List<LinhaReportVO> listarCaracteristicaSalaCirgPorUnidade(String pesquisa, Short unfSeq);

	public List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicoPorTipo(String strPesquisa);

	public Long listarMbcProcedimentoCirurgicoPorTipoCount(String strPesquisa);

	List<CirurgiasCanceladasVO> pesquisarCirurgiasCanceladas(String orderProperty, boolean asc,
		PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO) throws ApplicationBusinessException;
	
	List<AgendamentosExcluidosVO> pesquisarAgendamentosExcluidos(Integer firstResult, Integer maxResult,String orderProperty, boolean asc,
			PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO);
	
	Long pesquisarAgendamentosExcluidosCount(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO);

	void persistirMbcAgendaAnotacao(MbcAgendaAnotacao mbcAgendaAnotacao) throws ApplicationBusinessException;

	Integer pesquisarEprPciSeqporCirurgia(Integer agdSeq);
	String pesquisarProcEspCirurgico(Integer agdSeq) ;

	public void validarPesquisaPortalCirurgias(
			PortalPesquisaCirurgiasParametrosVO parametrosVO) throws ApplicationBusinessException;
	
	public List<EscalaSalasVO> pesquisarEscalaSalasPorUnidadeCirurgica(final Short unfSeq);

	public List<MbcAgendas> pesquisarAgendasPorPacienteEquipe(
			DominioSituacaoAgendas[] dominioSituacaoAgendas, Integer codigo,
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe,
			DominioFuncaoProfissional indFuncaoProfEquipe, Short seqEspecialidade,
			Short seqUnidFuncionalCirugica);

	public Boolean verificarExisteSolicEspApCongelacao(Integer agdSeq,
			BigDecimal vlrNumerico);

	public List<MbcProcPorEquipe> pesquisarProcedimentosEquipe(
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe);

	public List<MbcAgendaAnestesia> listarAgendaAnestesiaPorAgdSeq(Integer agdSeq);
	
	public List<MbcAgendaProcedimento> listarAgendaProcedimentoPorAgdSeq(Integer agdSeq);
	
	public MbcAgendaDiagnostico obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(Integer agdSeq);

	void validarAnestesiaAdicionadaExistente(List<MbcAgendaAnestesia> listaAgendaAnestesias, MbcAgendaAnestesia agendaAnestesia) throws ApplicationBusinessException;
	
	public List<VMbcProcEsp> pesquisarVMbcProcEspPorEsp(Object objParam, Short espSeq, Integer maxResults);
	
	public Long pesquisarVMbcProcEspPorEspCount(Object objParam, Short espSeq);
	
	public List<MbcAgendaProcedimento> pesquisarAgendaProcedimento(Integer agdSeq);
	
	public List<MbcAgendaSolicEspecial> buscarMbcAgendaSolicEspecialPorAgdSeq(Integer agdSeq);
	
	public List<AgendaCirurgiaSolicitacaoEspecialVO> buscarMbcAgendaCirurgiaSolicEspecialPorAgdSeq(Integer agdSeq);
	
	public List<MbcAgendaOrtProtese> buscarOrteseprotesePorAgenda(Integer agdSeq);
	
	public MbcAgendas obterAgendaPorAgdSeq(Integer agdSeq);
	
	public MbcAgendas buscarAgenda(Integer agdSeq);
	
	public List<MbcSalaCirurgica> buscarSalasDisponiveisParaTrocaNaAgenda(Date data, Short unfSeq, Short espSeq,
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf);
	
	public List<MbcProfAtuaUnidCirgs> buscarEquipeMedicaParaAgendamento(String nome, Short unfSeq, Short espSeq);
	
	public Long buscarEquipeMedicaParaAgendamentoCount(String nome, Short unfSeq, Short espSeq);

	public List<MbcProfAtuaUnidCirgs> buscarEquipeMedicaParaMudancaNaAgenda(String nome, Short unfSeq, Short espSeq);

	public Long buscarEquipeMedicaParaMudancaNaAgendaCount(String nome, Short unfSeq, Short espSeq);
	
	public List<MbcSalaCirurgica> buscarSalasCirurgicasPorUnfSeq(final Short unfSeq);

	public PortalPlanejamentoCirurgiasDiaVO pesquisarPortalPlanejamentoCirurgia(
			Short unfSeq, Date dataBase, AghEspecialidades especialidade, MbcProfAtuaUnidCirgs atuaUnidCirgs, MbcSalaCirurgica salaCirurgica, Boolean reverse, Integer countDias) throws ApplicationBusinessException, ApplicationBusinessException;

	public List<CirurgiasCanceladasAgendaMedicoVO> pesquisarCirgsCanceladasByMedicoEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer serMatricula, Short serVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) throws ApplicationBusinessException;

	public MbcProfAtuaUnidCirgs buscarEquipesPorUsuarioLogado() throws ApplicationBusinessException ;

	public Long pesquisarCirgsCanceladasByMedicoEquipeCount(Integer serMatricula, Short serVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo);
		
	

	public Long listarAgendaPorUnidadeEspecialidadeEquipePacienteCount(
			Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucIndFuncaoProf, Short espSeq,
			Short unfSeq, Integer pacCodigo);

	public List<MbcAgendas> listarAgendaPorUnidadeEspecialidadeEquipePaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional pucIndFuncaoProf,
			Short espSeq, Short unfSeq, Integer pacCodigo);

	void desatacharMbcAgendas(MbcAgendas mbcAgendas);
	
	public DominioTipoAgendaJustificativa retornarParametroCirurgiasCanceladas(Integer agdSeq) throws ApplicationBusinessException;
	

	Boolean listarEquipePlanejamentoCirurgiasPossuiRegistro(Date pDtIni,
			Date pDtFim, Integer pPucSerMatricula, Short pPucSerVinCodigo,
			Short pPucUnfSeq, DominioFuncaoProfissional pPucIndFuncaoProf,
			Short pEspSeq, Short pUnfSeq, String pEquipe)
			throws ApplicationBusinessException;

	public String obterResumoAgendamento(Date dtAgenda, MbcAgendas agenda);
	
	public void remarcarPacienteAgenda(Date dtReagendamento, MbcAgendas agenda, String justificativa, DominioTipoAgendaJustificativa dominio, MbcSalaCirurgica salaCirurgica) throws BaseException;
	
	public String obterNomeIntermediarioPacienteAbreviado(String nome);

	public void validarInclusaoPacienteAgenda(Date dtAgendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq , Short salaSeqp, String descricaoTurno) throws ApplicationBusinessException;

	public Boolean validarDataReagendamento(Date dtReagendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq);
	
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicasParaReagendamentoPaciente(Date dataAgenda, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short espSeq, Short unfSeq);

	MbcProfAtuaUnidCirgs obterEquipePorChavePrimaria(
			MbcProfAtuaUnidCirgsId idEquipe);
	
	public Boolean verificarEscalaDefinitivaFoiExecutada(Date dtReagendamento, Short unfSeq);

	public List<EscalaPortalPlanejamentoCirurgiasVO> pesquisarAgendasPlanejadas(
			Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional funProf, Short espSeq, Short unfSeq);
	
	public List<MbcSalaCirurgica> buscarSalasDisponiveisParaEscala(Date data, Short unfSeq, Short espSeq, 
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe, DominioFuncaoProfissional indFuncaoProf);
	
	public Date atualizaHoraInicioEscala(Date horaInicioEscala, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam, 
			Short espSeq, Short unfSeq, Short sciSeqp, Date dataAgenda) throws ApplicationBusinessException;
	
	public List<EscalaPortalPlanejamentoCirurgiasVO> pesquisarAgendasEmEscala(Date dtAgendaParam, Short sciUnfSeqCombo,
			Short sciSeqpCombo, Short unfSeqParam, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam);

	public void chamarTelaEscala(Date dtAgenda, Integer pucSerMatricula,
			Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucFuncProf, Short unfSeq, Short espSeq)
			throws ApplicationBusinessException;
	
	public String verificarRegimeMinimoSus(Integer seq);
	
	public void verificarHoraTurnoValido(
			
			Date dtAgendaParam, Short sciUnfSeqCombo, Short sciSeqpCombo,
			Short unfSeqParam, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam,
			Date horaEscala)
			throws  ApplicationBusinessException;

	public Long listarAghCaractUnidFuncionaisCount(String objPesquisa);

	public Long listarCaracteristicaSalaCirgPorUnidadeCount(String pesquisa, Short unfSeq);
	
	public PortalPesquisaCirurgiasAgendaEscalaDiaVO pesquisarAgendasEscalasDia(PortalPesquisaCirurgiasParametrosVO portalPesquisaCirurgiasParametrosVO, Date data, Boolean reverse) throws ApplicationBusinessException;
	
	public List<Date> buscarTodasDatasPaciente(PortalPesquisaCirurgiasParametrosVO parametros) throws ApplicationBusinessException ;
	
	public List<MbcSalaCirurgica> buscarSalasCirurgicasAtivasPorUnfSeqSeqp(Short unfSeq, Short seqp);
	
	public Long buscarSalasCirurgicasAtivasPorUnfSeqSeqpCount(Short unfSeq, Short seqp);

	MbcAgendas obterAgendaPorAgdSeq(Integer seq, Enum[] inner, Enum[] left);

	MbcAgendas obterAgendaPorSeq(Integer agdSeq);

	Set<MbcAgendaHemoterapia> listarAgendasHemoterapiaPorAgendaSeq(Integer agendaSeq);

	List<MbcAgendaDiagnostico> pesquisarAgendaDiagnosticoEscalaCirurgicaPorAgenda(Integer seq);
	
	void validaSituacaoAgenda(MbcAgendas agenda) throws ApplicationBusinessException;
	
	public MbcAgendaSolicEspecial obterMbcAgendaSolicEspecialPorNciSeqUnfseq(Integer agdSeq, Short nciSeq, Short seqp);
	
	public MbcNecessidadeCirurgica obterMbcNecessidadeCirurgicaPorId(Short seq, Enum[] innerJoins, Enum[] leftJoins);
	
	PortalPlanejamentoCirurgiasDiaVO pesquisarPortalPlanejamentoCirurgia(
			Short unfSeq, Date dataBase, AghEspecialidades especialidade,
			MbcProfAtuaUnidCirgs atuaUnidCirgs, MbcSalaCirurgica salaCirurgica,
			Boolean reverse, Integer countDias, Boolean otimizado)
			throws ApplicationBusinessException, ApplicationBusinessException;
			
	public MbcAgendas retornarAgendaPorAgdSeq(Integer agdSeq);

	List<MbcRequisicaoOpmes> consultarListaRequisicoesPorAgenda(Integer agdSeq);

	MbcAgendas obterAgendaRemarcarPorSeq(Integer agdSeq);
}