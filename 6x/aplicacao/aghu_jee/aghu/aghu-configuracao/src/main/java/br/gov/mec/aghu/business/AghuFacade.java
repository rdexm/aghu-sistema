package br.gov.mec.aghu.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;

import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.configuracao.dao.AghAlaDAO;
import br.gov.mec.aghu.configuracao.dao.AghArquivoProcessamentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghArquivoProcessamentoLogDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoJnDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoPacientesDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalAplicacaoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.configuracao.dao.AghCapitulosCidDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractUnidFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractUnidFuncionaisJnDAO;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.configuracao.dao.AghClinicasDAO;
import br.gov.mec.aghu.configuracao.dao.AghCoresTabelasSistemaDAO;
import br.gov.mec.aghu.configuracao.dao.AghDocumentoContingenciaDAO;
import br.gov.mec.aghu.configuracao.dao.AghDocumentosAssinadosDAO;
import br.gov.mec.aghu.configuracao.dao.AghEquipesDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesJnDAO;
import br.gov.mec.aghu.configuracao.dao.AghFeriadosDAO;
import br.gov.mec.aghu.configuracao.dao.AghGrupoCidsDAO;
import br.gov.mec.aghu.configuracao.dao.AghHorariosUnidFuncionalDAO;
import br.gov.mec.aghu.configuracao.dao.AghImpressoraPadraoUnidsDAO;
import br.gov.mec.aghu.configuracao.dao.AghInstituicoesHospitalaresDAO;
import br.gov.mec.aghu.configuracao.dao.AghNodoPolDAO;
import br.gov.mec.aghu.configuracao.dao.AghOrigemEventosDAO;
import br.gov.mec.aghu.configuracao.dao.AghParametroAplicacaoDAO;
import br.gov.mec.aghu.configuracao.dao.AghProfEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghProfissionaisEquipeDAO;
import br.gov.mec.aghu.configuracao.dao.AghProfissionaisEspConvenioDAO;
import br.gov.mec.aghu.configuracao.dao.AghTabelasSistemaDAO;
import br.gov.mec.aghu.configuracao.dao.AghTiposUnidadeFuncionalDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisJnDAO;
import br.gov.mec.aghu.configuracao.dao.VAghUnidFuncionalDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.ObjetosOracleException;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioCategoriaTabela;
import br.gov.mec.aghu.dominio.DominioMesFeriado;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.DominioVivoMorto;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.solicitacao.vo.AtendimentoSolicExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameFilter;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.faturamento.vo.ReimpressaoLaudosProcedimentosVO;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.internacao.vo.SituacaoLeitosVO;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghAtendimentoJn;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacaoId;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghCaractEspecialidadesId;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisJn;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghCoresTabelasSistema;
import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.model.AghDocumentosAssinados;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidadesJn;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghHorariosUnidFuncionalId;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnidsId;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghLogAplicacao;
import br.gov.mec.aghu.model.AghNodoPol;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenioId;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.model.AghTabelasSistema;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AghUnidadesFuncionaisJn;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.model.VAghUnidFuncionalId;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoAtendimentoEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioQuimioPOLVO;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosRegistroCivilVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalPacienteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.AghAtendimentosPacienteCnsVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.vo.AtendimentoNascimentoVO;
import br.gov.mec.aghu.vo.ContagemQuimioterapiaVO;
import br.gov.mec.aghu.vo.DadosPacientesEmAtendimentoVO;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;
import br.gov.mec.aghu.vo.RapServidoresVO;


@Modulo(ModuloEnum.CONFIGURACAO)
//@InjetarLogin
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.NcssTypeCount",
		"PMD.CouplingBetweenObjects" })
@Stateless
public class AghuFacade extends BaseFacade implements IAghuFacade {
 
	@EJB
	private DocumentoContingenciaON documentoContingenciaON;
	
	@EJB
	private CidON cidON;
	
	@EJB
	private AghUnidadesFuncionaisRN aghUnidadesFuncionaisRN;
	
	@EJB
	private AghProfEspecialidadesRN aghProfEspecialidadesRN;
	
	@EJB
	private RemoverLogsAplicacaoSchedulerRN removerLogsAplicacaoSchedulerRN;
	
	@EJB
	private AghPesquisaMenuLogRN aghPesquisaMenuLogRN;
	
	@EJB
	private AghuConfigRN aghuConfigRN;
	
	@EJB
	private EspecialidadeON especialidadeON;
	
	@EJB
	private TabelasSistemaCRUD tabelasSistemaCRUD;
	
	@EJB
	private ManterFeriadoRN manterFeriadoRN;
	
	@EJB
	private AghLogAplicacaoON aghLogAplicacaoON;
	
	@EJB
	private AghEspecialidadeON aghEspecialidadeON;
	
	@EJB
	private CaixaPostalServidorON caixaPostalServidorON;
	
	@EJB
	private EquipeCRUD equipeCRUD;

	@EJB
	private AghAtendimentoON aghAtendimentoON;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AghCaractUnidFuncionaisDAO aghCaractUnidFuncionaisDAO;
	
	@Inject
	private AghCaixaPostalDAO aghCaixaPostalDAO;
	
	@Inject
	private AghOrigemEventosDAO aghOrigemEventosDAO;
	
	@Inject
	private AghUnidadesFuncionaisJnDAO aghUnidadesFuncionaisJnDAO;
	
	@Inject
	private AghArquivoProcessamentoLogDAO aghArquivoProcessamentoLogDAO;
	
	@Inject
	private AghProfissionaisEquipeDAO aghProfissionaisEquipeDAO;
	
	@Inject
	private AghCaixaPostalAplicacaoDAO aghCaixaPostalAplicacaoDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;
	
	@Inject
	private AghDocumentoContingenciaDAO aghDocumentoContingenciaDAO;
	
	@Inject
	private VAghUnidFuncionalDAO vAghUnidFuncionalDAO;
	
	@Inject
	private AghFeriadosDAO aghFeriadosDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private AghProfEspecialidadesDAO aghProfEspecialidadesDAO;
	
	@Inject
	private AghCapitulosCidDAO aghCapitulosCidDAO;
	
	@Inject
	private AghAtendimentoJnDAO aghAtendimentoJnDAO;
	
	@Inject
	private AghParametroAplicacaoDAO aghParametroAplicacaoDAO;
	
	@Inject
	private AghDocumentosAssinadosDAO aghDocumentosAssinadosDAO;
	
	@Inject
	private AghInstituicoesHospitalaresDAO aghInstituicoesHospitalaresDAO;
	
	@Inject
	private AghClinicasDAO aghClinicasDAO;
	
	@Inject
	private AghNodoPolDAO aghNodoPolDAO;
	
	@Inject
	private AghTabelasSistemaDAO aghTabelasSistemaDAO;
	
	@Inject
	private AghCaractEspecialidadesDAO aghCaractEspecialidadesDAO;
	
	@Inject
	private AghEquipesDAO aghEquipesDAO;
	
	@Inject
	private AghGrupoCidsDAO aghGrupoCidsDAO;
	
	@Inject
	private AghEspecialidadesJnDAO aghEspecialidadesJnDAO;
	
	@Inject
	private AghCoresTabelasSistemaDAO aghCoresTabelasSistemaDAO;
	
	@Inject
	private AghAlaDAO aghAlaDAO;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@Inject
	private AghHorariosUnidFuncionalDAO aghHorariosUnidFuncionalDAO;
	
	@Inject
	private AghImpressoraPadraoUnidsDAO aghImpressoraPadraoUnidsDAO;
	
	@Inject
	private AghCidDAO aghCidDAO;
	
	@Inject
	private AghAtendimentoPacientesDAO aghAtendimentoPacientesDAO;
	
	@Inject
	private AghTiposUnidadeFuncionalDAO aghTiposUnidadeFuncionalDAO;
	
	@Inject
	private AghArquivoProcessamentoDAO aghArquivoProcessamentoDAO;
	
	@Inject
	private AghCaractUnidFuncionaisJnDAO aghCaractUnidFuncionaisJnDAO;
	
	@Inject
	private AghProfissionaisEspConvenioDAO aghProfissionaisEspConvenioDAO;

	private static final long serialVersionUID = 1426576161328219683L;

	@Override
	public List<AghAla> buscarTodasAlas() {
		return this.getAghAlaDAO().listarTodos();
	}

	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacade#obterAtendimentoJustificativaUsoPorId(java.lang.Integer)
	 */
	@Override
	public AghAtendimentos obterAtendimentoJustificativaUsoPorId(Integer atdSeq) {

		return getAghAtendimentoDAO().obterAtendimentoJustificativaUsoPorId(atdSeq);
	}
	
	protected AghAlaDAO getAghAlaDAO() {
		return aghAlaDAO;
	}

	protected AghuConfigRN getAghuConfigRN() {
		return aghuConfigRN;
	}
	
	protected AghPesquisaMenuLogRN getAghPesquisaMenuLogRN() {
		return aghPesquisaMenuLogRN;
	}

	protected EspecialidadeON getEspecialidadeON() {
		return especialidadeON;
	}

	@Override
	public boolean isOracle(){
		return aghUnidadesFuncionaisDAO.isOracle();
	}
	
	@Override
	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaract(Short seq) {
		return this.getAghUnidadesFuncionaisDAO()
				.obterUnidadesFuncionaisHierarquicasPorCaract(seq, // Valor
																	// default
																	// antes da
																	// sobrecarga
						ConstanteAghCaractUnidFuncionais.CONTROLA_UNID_PAI);
	}

	@Override
	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaract2(Short seq,
			ConstanteAghCaractUnidFuncionais cacuf) {
		return this.getAghUnidadesFuncionaisDAO()
				.obterUnidadesFuncionaisHierarquicasPorCaract2(seq, cacuf);
	}

	@Override
	public RapPessoasFisicas obterEspecialidadeInternacaoServidorChefePessoaFisica(final Short seq){
		return getEspecialidadeON().obterEspecialidadeInternacaoServidorChefePessoaFisica(seq);
	}
	
	@Override
	public AghUnidadesFuncionais obterUnidadeFuncionalComCaracteristica(final Short seq) {
		return this.getAghUnidadesFuncionaisDAO().obterUnidadeFuncionalComCaracteristica(seq);
	}
	
	@Override
	public AghCaixaPostalAplicacao inserirAghCaixaPostalAplicacao(
			AghCaixaPostalAplicacao caixaPostalAp, boolean flush) {
		this.getAghCaixaPostalAplicacaoDAO().persistir(caixaPostalAp);
		if (flush) {
			this.getAghCaixaPostalAplicacaoDAO().flush();
		}
		return caixaPostalAp;
	}

	@Override
	public List<AghCaixaPostal> pesquisarMensagemPendenciasCaixaPostal(
			final RapServidores servidor) {
		return this.getAghCaixaPostalServidorDAO()
				.pesquisarMensagemPendenciasCaixaPostal(servidor);
	}

	@Override
	@Secure("#{s:hasPermission('identificarUnidadeExecutora','pesquisar')}")
	public Long pesquisarAghUnidadesFuncionaisCount(
			final AghUnidadesFuncionais aghUnidadesFuncionais) {
		return getAghUnidadesFuncionaisDAO().pesquisarCount(
				aghUnidadesFuncionais);
	}

	@Override
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionaisAtivasColetaveis(
			final Object parametro) {

		final List<AghUnidadesFuncionais> listaAghUnidadesFuncionais = getAghCaractUnidFuncionaisDAO()
				.obterListaUnidadesFuncionaisAtivasPorCaracteristica(
						ConstanteAghCaractUnidFuncionais.UNID_COLETA);

		return getAghUnidadesFuncionaisDAO()
				.listarAghUnidadesFuncionaisAtivasColetaveis(parametro,
						listaAghUnidadesFuncionais);
	}
	
	@Override
	public List<AghCaractUnidFuncionais> listarCaractUnidFuncionaisEUnidadeFuncional(String objPesquisa, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais){
		return getAghCaractUnidFuncionaisDAO().listarCaractUnidFuncionaisEUnidadeFuncional(objPesquisa, constanteAghCaractUnidFuncionais);
	}
	
	@Override
	public List<Short> verificaPrimeiraPrescricaoMedicaPacienteUnidFuncional(Integer atdSeq, String parametro) {
		return getAghCaractUnidFuncionaisDAO().verificaPrimeiraPrescricaoMedicaPacienteUnidFuncional(atdSeq, parametro);
	}
	
	@Override
	public List<Short> pesquisarUnidadesFuncionaisPorCaracteristica(String parametro) {
		return getAghCaractUnidFuncionaisDAO().pesquisarUnidadesFuncionaisPorCaracteristica(parametro);
	}
	
	@Override
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionaisAtivasCirurgicas(final Object parametro) {

		final List<AghUnidadesFuncionais> listaAghUnidadesFuncionais = getAghCaractUnidFuncionaisDAO().obterListaUnidadesFuncionaisAtivasPorCaracteristica(
				ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS);
		
		List<AghUnidadesFuncionais.Fields> fieldsOrder = new ArrayList<AghUnidadesFuncionais.Fields>();
		fieldsOrder.add(AghUnidadesFuncionais.Fields.SEQUENCIAL);

		final List<Short> setSeqs = new ArrayList<Short>();

		if (listaAghUnidadesFuncionais != null) {
			for (final AghUnidadesFuncionais aghUnidadesFuncionais : listaAghUnidadesFuncionais) {
				setSeqs.add(aghUnidadesFuncionais.getSeq());
			}
		}
		
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(parametro, DominioSituacao.A, true, true, true, fieldsOrder, setSeqs);				
	}

	@Override
	public List<AghHorariosUnidFuncional> pesquisarAghHorariosUnidFuncional(
			final Short unfSeq, final DominioTipoDia tipoDia,
			final Date hrInicial, final Date hrFinal, final Boolean indPlantao,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {
		return getAghHorariosUnidFuncionalDAO()
				.pesquisarAghHorariosUnidFuncional(unfSeq, tipoDia, hrInicial,
						hrFinal, indPlantao, firstResult, maxResult,
						orderProperty, asc);
	}

	@Override
	public Long pesquisarAghHorariosUnidFuncionalCount(final Short unfSeq,
			final DominioTipoDia tipoDia, final Date hrInicial,
			final Date hrFinal, final Boolean indPlantao) {
		return getAghHorariosUnidFuncionalDAO()
				.pesquisarAghHorariosUnidFuncionalCount(unfSeq, tipoDia,
						hrInicial, hrFinal, indPlantao);
	}

	@Override
	public List<AghUnidadesFuncionais> listarAghUnidadesFuncionais(
			final Object parametro) {
		return getAghUnidadesFuncionaisDAO().listarAghUnidadesFuncionais(
				parametro);
	}

	// #5443 - Identificar Unidades executoras
	@Override
	@Secure("#{s:hasPermission('identificarUnidadeExecutora','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarDadosBasicosUnidadesExecutoras(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final AghUnidadesFuncionais elemento) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesExecutoras(
				firstResult, maxResult, orderProperty, asc, elemento);
	}

	@Override
	public AghUnidadesFuncionais obterAghUnidFuncionaisPeloId(final Short codigo) {
		return this.getAghUnidadesFuncionaisDAO().obterPeloId(codigo);
	}
	
	@Override
	public AghUnidadesFuncionais obterAghUnidFuncionaisPorUnfSeq(final Short unfSeq) {
		return this.getAghUnidadesFuncionaisDAO().obterPorUnfSeq(unfSeq);
	}	

	@Override
	@Secure("#{s:hasPermission('identificarUnidadeExecutora','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasPorCodigoOuDescricao(
			final Object elemento) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesExecutorasPorCodigoOuDescricao(elemento);
	}

	@Override
	public Long pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(
			final Object elemento) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(elemento);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasPorSeqDescricao(String parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}

    @Override
    public List<AghUnidadesFuncionais> obterUnidadesFuncionaisListaUnidadesSolicitacao(String parametro, List<Short> seqUnidades){
        return getAghUnidadesFuncionaisDAO().obterUnidadesFuncionaisListaUnidadesSolicitacao(parametro, seqUnidades);
    }


	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasExecutoraColeta(
			final Object parametro) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisAtivasExecutoraColeta(parametro);
	}


	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidExecPorCodDescCaractExames(
			final Object elemento) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidExecPorCodDescCaractExames(elemento);
	}
	
	private CaixaPostalServidorON getCaixaPostalServidorON() {
		return caixaPostalServidorON;
	}
	
	@Override
	public AghCaixaPostalServidor obterAghCaixaPostalServidor(
			AghCaixaPostalServidorId id) {
		return getAghCaixaPostalServidorDAO().obterPorChavePrimaria(id);
	}

	@Override
	public AghCaixaPostalServidor atualizarAghCaixaPostalServidor(
			AghCaixaPostalServidor caixaPostalServ) {
		return getAghCaixaPostalServidorDAO().atualizar(caixaPostalServ);
	}

	@Override
	public void persistirAghCaixaPostalServidor(
			AghCaixaPostalServidor caixaPostalServ) {
		getAghCaixaPostalServidorDAO().persistir(caixaPostalServ);
	}
	
	@Override
	public void persistirAghCaixaPostal(AghCaixaPostal caixaPostal) {
		getAghCaixaPostalDAO().persistir(caixaPostal);
	}
	
	@Override
	public void atualizarAghCaixaPostal(AghCaixaPostal caixaPostal){
		getAghCaixaPostalDAO().atualizar(caixaPostal);
	}

	@Override
	public void excluirMensagemCxPostServidor(AghCaixaPostalServidorId id) {
		getCaixaPostalServidorON().excluirMensagem(id);
	}
	
	private AghCaixaPostalServidorDAO getAghCaixaPostalServidorDAO() {
		return aghCaixaPostalServidorDAO;
	}
	
	private AghCaixaPostalDAO getAghCaixaPostalDAO(){
		return aghCaixaPostalDAO;
	}

	private AghCaixaPostalAplicacaoDAO getAghCaixaPostalAplicacaoDAO() {
		return aghCaixaPostalAplicacaoDAO;
	}

	@Override
	public List<AghParametroAplicacao> pesquisarParametroAplicacaoPorCaixaPostalAplicacao(
			AghCaixaPostalAplicacaoId id) {
		return getAghParametroAplicacaoDAO()
				.pesquisarParametroAplicacaoPorCaixaPostalAplicacao(id);
	}

	@Override
	public AghParametroAplicacao inserirAghParametroAplicacao(
			AghParametroAplicacao elemento, boolean flush) {
		getAghParametroAplicacaoDAO().persistir(elemento);
		if (flush){
			getAghParametroAplicacaoDAO().flush();
		}
		return elemento;
	}

	protected AghParametroAplicacaoDAO getAghParametroAplicacaoDAO() {
		return aghParametroAplicacaoDAO;
	}

	@Override
	public List<AghAtendimentos> listarAtendimentoEmAnadamentoConsulta(
			Integer numeroConsulta) {
		return getAghAtendimentoDAO().listarAtendimentoEmAnadamentoConsulta(
				numeroConsulta);
	}

	protected AghAtendimentoDAO getAghAtendimentoDAO() {
		return aghAtendimentoDAO;
	}

	/**
	 * Retorna Atendimento pela PK, apenas se este tiver
	 * a devida associação com:
	 * Unidade Funcional e Centro de Custo.
	 */
	@Override
	public AghAtendimentos obterAghAtendimentoPorChavePrimaria(
			Integer chavePrimaria) {
		return this.getAghAtendimentoDAO().obterDetalhadoPorChavePrimaria(chavePrimaria);
	}
	
	@Override
	public AtendimentoSolicExameVO obterAtendimentoSolicExameVO(
			Integer atdSeq) {
		return this.getAghAtendimentoDAO().obterAtendimentoSolicExameVO(atdSeq);
	}
	
	@Override
	public AghAtendimentos obterAghAtendimentoParaSolicitacaoConsultoria(
			Integer chavePrimaria) {
		return this.getAghAtendimentoDAO().obterAghAtendimentoParaSolicitacaoConsultoria(chavePrimaria);
	}
	
	@Override
	public boolean verificarPacienteInternadoCaracteristicaControlePrevisao(List<Short> unidadesFuncionaisSeq, Integer atendimentoSeq) throws ApplicationBusinessException {
		return this.getAghAtendimentoDAO().verificarPacienteInternadoCaracteristicaControlePrevisao(unidadesFuncionaisSeq, atendimentoSeq);
	}

	@Override
	public Date obterDthrFimAtendimento(Integer atdseq){
		return aghAtendimentoDAO.obterDthrFimAtendimento(atdseq);
	}

	@Override
	public AghAtendimentos obterOriginal(AghAtendimentos aghAtendimento) {
		return this.getAghAtendimentoDAO().obterOriginal(aghAtendimento);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, String seqs) {
		return getAghAtendimentoDAO().pesquisarAtendimentoAmbPorPacCodigo(
				firstResult, maxResult, orderProperty, asc, codigo, seqs);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim(
			Integer codigo, Date dtIni, Date dtFim) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim(
						codigo, dtIni, dtFim);
	}

	@Override
	public Long pesquisarAtendimentoAmbPorPacCodigoCount(Integer codigo,
			String seqs) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoAmbPorPacCodigoCount(codigo, seqs);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoAmbCronologicoPorPacCodigo(
			Integer codigo) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoAmbCronologicoPorPacCodigo(codigo);
	}

	@Override
	public boolean verificarExisteAtendimentoAmbCronologicoPorPacCodigo(
			Integer codigo) {
		return getAghAtendimentoDAO()
				.verificarExisteAtendimentoAmbCronologicoPorPacCodigo(codigo);
	}

	@Override
	public List<AghEspecialidades> pesquisarPorNomeSiglaInternaUnidade(
			String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeSiglaInternaUnidade(
				parametro);
	}

	@Override
	public List<SuggestionListaCirurgiaVO> pesquisarEspecialidadesCirurgicas(final String filtro, final AghUnidadesFuncionais unidade, final Date data, final DominioSituacao indSituacao){
		return getAghEspecialidadesDAO().pesquisarEspecialidadesCirurgicas(filtro, unidade, data, indSituacao);
	}

	@Override
	public Long pesquisarEspecialidadesCirurgicasCount(final String filtro, final AghUnidadesFuncionais unidade, final Date data, final DominioSituacao indSituacao){
		return getAghEspecialidadesDAO().pesquisarEspecialidadesCirurgicasCount(filtro, unidade, data, indSituacao);
	}
	
	@Override
	public Long pesquisarPorNomeSiglaInternaUnidadeCount(String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeSiglaInternaUnidadeCount(parametro);
	}

	protected AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO() {
		return aghUnidadesFuncionaisDAO;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivas() {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisAtivas();
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadeFuncionalPorFuncionalSala(
			Object objPesquisa) {
		return getAghUnidadesFuncionaisDAO()
				.listarUnidadeFuncionalPorFuncionalSala(objPesquisa,
						DominioSituacao.A);
	}

	@Override
	public List<VAacSiglaUnfSala> pesquisarSalasUnidadeFuncional(
			Object objPesquisa, AghUnidadesFuncionais unidadeFuncional) {
		return getAghUnidadesFuncionaisDAO().pesquisarSalasUnidadeFuncional(
				objPesquisa, unidadeFuncional, DominioSituacao.A);
	}

	@Override
	public List<VAacSiglaUnfSala> pesquisarSalasUnidadesFuncionais(
			List<AghUnidadesFuncionais> undsFuncionais, DominioSituacao situacao) {
		return getAghUnidadesFuncionaisDAO().pesquisarSalasUnidadesFuncionais(
				undsFuncionais, situacao);
	}

	@Override
	public AghUnidadesFuncionais obterUnidadeFuncionalPorChavePrimaria(
			AghUnidadesFuncionais unidadeFuncional) {
		return getAghUnidadesFuncionaisDAO().obterPorChavePrimaria(
				unidadeFuncional.getSeq());
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(
			Object parametro, Integer maxResults) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncional(
				parametro, maxResults);
	}

	protected AghEspecialidadesDAO getAghEspecialidadesDAO() {
		return aghEspecialidadesDAO;
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidades(Object objPesquisa) {
		return getAghEspecialidadesDAO().pesquisarEspecialidades(objPesquisa);
	}
	
	@Override
	public Long pesquisarEspecialidadesCount(Object objPesquisa) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesCount(objPesquisa);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesPorNomeOuSigla(
			String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeOuSigla(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeFluxogramaPorNomeOuSigla(
			String parametro) {
		return getAghEspecialidadesDAO()
				.pesquisarEspecialidadeFluxogramaPorNomeOuSigla(parametro);
	}

	@Override
	public Long pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(String parametro) {
		return getAghEspecialidadesDAO()
				.pesquisarEspecialidadeFluxogramaPorNomeOuSiglaCount(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSigla(
			String parametro) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadePorNomeOuSigla(
				parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(String parametro) {
		return getAghEspecialidadesDAO().pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(parametro);
	}
	
	@Override
	public Long pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEspCount(String parametro) {
		return getAghEspecialidadesDAO().pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEspCount(parametro);
	}	

	@Override
	public List<AghEspecialidades> listarEspecialidadePorNomeOuSigla(String parametro) {
		return getAghEspecialidadesDAO().listarEspecialidadePorNomeOuSigla(parametro);
	}
	
	@Override
	public Long listarEspecialidadePorNomeOuSiglaCount(String parametro) {
		return getAghEspecialidadesDAO().listarEspecialidadePorNomeOuSiglaCount(parametro);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeAtivaPorNomeOuSigla(String parametro, Integer maxResults){
		return getAghEspecialidadesDAO().pesquisarEspecialidadeAtivaPorNomeOuSigla(parametro, maxResults);
	}
	
	@Override
	public Integer pesquisarEspecialidadeAtivaPorNomeOuSiglaCount(String parametro){
		return getAghEspecialidadesDAO().pesquisarEspecialidadeAtivaPorNomeOuSiglaCount(parametro);
	}
		
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadePrincipalAtivaPorNomeOuSigla(String parametro, Integer maxResults){
		return getAghEspecialidadesDAO().pesquisarEspecialidadePrincipalAtivaPorNomeOuSigla(parametro, maxResults);
	}
	
	/**
	 * Consulta atendimentos filtrando pelo id da conta hospitalar de
	 * determinada conta internacao.
	 */
	@Override
	public List<AghAtendimentos> obterAtendimentosDeContasInternacaoPorContaHospitalar(
			Integer seqCth) {
		return getAghAtendimentoDAO()
				.obterAtendimentosDeContasInternacaoPorContaHospitalar(seqCth);
	}

	@Override
	public List<AghCaractUnidFuncionais> listarCaracteristicasUnidadesFuncionais(
			Short unfSeq, ConstanteAghCaractUnidFuncionais[] caracteristicas,
			Integer firstResult, Integer maxResults) {
		return getAghCaractUnidFuncionaisDAO()
				.listarCaracteristicasUnidadesFuncionais(unfSeq,
						caracteristicas, firstResult, maxResults);
	}

	@Override
	public AghCid obterAghCidsPorChavePrimaria(Integer chavePrimaria) {
		return getAghCidsDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public void removerAghCid(AghCid aghCid) {
		aghCid = aghCidDAO.obterPorChavePrimaria(aghCid.getSeq());
		aghCidDAO.remover(aghCid);
	}

	@Override
	public AghCid atualizarAghCid(AghCid aghCid) {
		AghCid retorno = aghCidDAO.atualizar(aghCid);
		return retorno;
	}

	@Override
	public AghCid inserirAghCid(AghCid aghCid) {
		aghCidDAO.persistir(aghCid);
		return aghCid;
	}

	@Override
	public String buscaPrimeiroCidContaSituacao(Integer seq, DominioSituacao situacao) {
		return getAghCidsDAO().buscaPrimeiroCidContaSituacao(seq, situacao);
	}

	@Override
	public AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short chavePrimaria) {
		return getAghEspecialidadesDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short chavePrimaria, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getAghEspecialidadesDAO().obterPorChavePrimaria(chavePrimaria, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	@Override
	public AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria) {
		return getAghUnidadesFuncionaisDAO().obterPorChavePrimaria(chavePrimaria);
	}

	@Override
	public AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(
			Short chavePrimaria, Enum... alias) {
		return getAghUnidadesFuncionaisDAO().obterPorChavePrimaria(
				chavePrimaria, alias);
	}

	@Override
	public AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria, Enum[] aliasInner, Enum[] aliasLeft) {
		return obterAghUnidadesFuncionaisPorChavePrimaria(chavePrimaria, false, aliasInner, aliasLeft);
	}
	
	@Override
	public AghUnidadesFuncionais obterAghUnidadesFuncionaisPorChavePrimaria(Short chavePrimaria, boolean inicializarCaracteristicas, Enum[] aliasInner, Enum[] aliasLeft) {
		AghUnidadesFuncionais unid= getAghUnidadesFuncionaisDAO().obterPorChavePrimaria(chavePrimaria, aliasInner, aliasLeft);
		
		if(inicializarCaracteristicas){
			Hibernate.initialize(unid.getCaracteristicas());
		}
		
		return unid;
	}

	@Override
	public void associarAghCaractUnidFuncionais(AghCaractUnidFuncionais acuf){
		getAghCaractUnidFuncionaisDAO().persistir(acuf);
	}

	@Override
	public void desassociarAghCaractUnidFuncionais(AghCaractUnidFuncionaisId id){
		getAghCaractUnidFuncionaisDAO().removerPorId(id);
	}
	
	protected AghCaractUnidFuncionaisDAO getAghCaractUnidFuncionaisDAO() {
		return aghCaractUnidFuncionaisDAO;
	}

	protected AghCidDAO getAghCidsDAO() {
		return aghCidDAO;
	}

	protected AghArquivoProcessamentoDAO getAghArquivoProcessamentoDAO() {
		return aghArquivoProcessamentoDAO;
	}

	protected AghArquivoProcessamentoLogDAO getAghArquivoProcessamentoLogDAO() {
		return aghArquivoProcessamentoLogDAO;
	}

	@Override
	public List<AghArquivoProcessamento> pesquisarArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(
			String sigla, List<Integer> arquivos) {
		return getAghArquivoProcessamentoDAO()
				.pesquisarArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(
						sigla, arquivos);
	}

	@Override
	public AghSistemas obterAghSistema(String sigla) {
		return getAghArquivoProcessamentoDAO().obterAghSistema(sigla);
	}

	@Override
	public AghArquivoProcessamento persistirAghArquivoProcessamento(
			AghArquivoProcessamento aghArquivo) {
		getAghArquivoProcessamentoDAO().persistir(aghArquivo);
		getAghArquivoProcessamentoDAO().flush();
		return aghArquivo;
	}

	@Override
	public AghArquivoProcessamento obterArquivoNaoProcessado(String sigla,
			String nome) {
		return getAghArquivoProcessamentoDAO().obterArquivoNaoProcessado(sigla,
				nome);
	}

	@Override
	public AghArquivoProcessamento obterArquivoNaoProcessado(
			AghSistemas sistema, String nome) {
		return getAghArquivoProcessamentoDAO().obterArquivoNaoProcessado(
				sistema, nome);
	}

	@Override
	public List<AghArquivoProcessamento> pesquisarArquivosAbortados(
			final AghSistemas sistema, Integer minutosVencimento) {
		return getAghArquivoProcessamentoDAO().pesquisarArquivosAbortados(
				sistema, minutosVencimento);
	}

	@Override
	public String pesquisarLogsPorArquivosIds(List<Integer> arquivos) {
		return getAghArquivoProcessamentoLogDAO().pesquisarLogsPorArquivosIds(
				arquivos, null);
	}	
	
	@Override
	public String pesquisarLogsPorArquivosIds(List<Integer> arquivos, Integer maxLength) {
		return getAghArquivoProcessamentoLogDAO().pesquisarLogsPorArquivosIds(
				arquivos,maxLength);
	}

	@Override
	public AghArquivoProcessamento obterAghArquivoProcessamentoPorChavePrimaria(
			Integer seq) {
		return getAghArquivoProcessamentoDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<ReimpressaoLaudosProcedimentosVO> listarReimpressaoLaudosProcedimentosVO(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pacCodigo, Integer pacProntuario) {
		return this.getAghAtendimentoDAO()
				.listarReimpressaoLaudosProcedimentosVO(firstResult, maxResult,
						orderProperty, asc, pacCodigo, pacProntuario);
	}

	@Override
	public Long listarReimpressaoLaudosProcedimentosVOCount(
			Integer pacCodigo, Integer pacProntuario) {
		return this.getAghAtendimentoDAO()
				.listarReimpressaoLaudosProcedimentosVOCount(pacCodigo,
						pacProntuario);
	}

	@Override
	public Long pesquisarClinicasCount(String filtro) {
		return getAghClinicasDAO().pesquisarClinicasCount(filtro);
	}

	protected AghClinicasDAO getAghClinicasDAO() {
		return aghClinicasDAO;
	}

	@Override
	public AghClinicas pesquisarAghClinicasPorCodigo(final Integer codigo) {
		return getAghClinicasDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesAgendas(String filtro) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadesAgendas(
				filtro);
	}

	@Override
	public Long pesquisarEspecialidadesAgendasCount(String filtro) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadesAgendasCount(filtro);
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(java.lang.Object,
	 *      br.gov.mec.aghu.dominio.DominioSituacao, java.lang.Boolean,
	 *      java.lang.Boolean, java.lang.Boolean, java.util.List,
	 *      br.gov.mec.aghu.core.constante.ConstanteAghCaractUnidFuncionais)
	 */
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametroPesquisa, DominioSituacao situacao,
			Boolean buscarPorCodigo, Boolean buscarPorDescricao,
			Boolean orderAsc,
			List<AghUnidadesFuncionais.Fields> atributosOrder,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas) {

		List<Short> listaUnfSeq = new ArrayList<Short>();
		if (listaCaracteristicas != null && listaCaracteristicas.length > 0) {
			listaUnfSeq.addAll(getIdfUnfCaracteristicas(listaCaracteristicas));
		}

		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						parametroPesquisa, situacao, buscarPorCodigo,
						buscarPorDescricao, orderAsc, atributosOrder,
						listaUnfSeq);
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(java.lang.Object,
	 *      br.gov.mec.aghu.dominio.DominioSituacao, java.lang.Boolean,
	 *      java.lang.Boolean, java.lang.Boolean,
	 *      br.gov.mec.aghu.core.constante.ConstanteAghCaractUnidFuncionais)
	 */
	@Override
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
			Object parametroPesquisa, DominioSituacao situacao,
			Boolean buscarPorCodigo, Boolean buscarPorDescricao,
			Boolean orderAsc,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas) {

		List<Short> listaUnfSeq = new ArrayList<Short>();
		if (listaCaracteristicas != null && listaCaracteristicas.length > 0) {
			listaUnfSeq.addAll(getIdfUnfCaracteristicas(listaCaracteristicas));
		}

		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
						parametroPesquisa, situacao, buscarPorCodigo,
						buscarPorDescricao, orderAsc, listaUnfSeq);
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(java.lang.Object,
	 *      br.gov.mec.aghu.dominio.DominioSituacao,
	 *      br.gov.mec.aghu.model.AghUnidadesFuncionais.Fields,
	 *      br.gov.mec.aghu.core.constante.ConstanteAghCaractUnidFuncionais)
	 */
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametroPesquisa, DominioSituacao situacao,
			AghUnidadesFuncionais.Fields atributoOrder,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas) {

		List<Short> listaUnfSeq = new ArrayList<Short>();
		if (listaCaracteristicas != null && listaCaracteristicas.length > 0) {
			listaUnfSeq.addAll(getIdfUnfCaracteristicas(listaCaracteristicas));
		}

		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						parametroPesquisa, situacao, atributoOrder, listaUnfSeq);
	}

	/**
	 * Pesquisa Unidades funcionais por código/ descrição/ Ala e Andar,
	 * filtrando também por características específicas
	 * 
	 * @param parametroPesquisa
	 * @param situacao
	 * @param List
	 *            <atributoOrder>
	 * @param listaCaracteristicas
	 * @return List<AghUnidadesFuncionais> (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(java.lang.Object,
	 *      br.gov.mec.aghu.dominio.DominioSituacao, java.util.List,
	 *      br.gov.mec.aghu.core.constante.ConstanteAghCaractUnidFuncionais)
	 */
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(
			Object parametroPesquisa, DominioSituacao situacao,
			List<AghUnidadesFuncionais.Fields> atributoOrder,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas) {

		List<Short> listaUnfSeq = new ArrayList<Short>();
		if (listaCaracteristicas != null && listaCaracteristicas.length > 0) {
			listaUnfSeq.addAll(getIdfUnfCaracteristicas(listaCaracteristicas));
		}

		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(
						parametroPesquisa, situacao, atributoOrder, listaUnfSeq);
	}

	@Override
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
			String parametroPesquisa, DominioSituacao situacao,
			ConstanteAghCaractUnidFuncionais... listaCaracteristicas) {
		List<Short> listaUnfSeq = new ArrayList<Short>();
		if (listaCaracteristicas != null && listaCaracteristicas.length > 0) {
			listaUnfSeq.addAll(getIdfUnfCaracteristicas(listaCaracteristicas));
		}
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
						parametroPesquisa, situacao, listaUnfSeq);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisColetaPorCodigoDescricao(final Object parametroPesquisa) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisColetaPorCodigoDescricao(parametroPesquisa);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorNumeroConsulta(
			Integer consultaNumero) {
		return getAghAtendimentoDAO().listarAtendimentosPorNumeroConsulta(
				consultaNumero);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorNumeroConsultaOrdenado(
			Integer consultaNumero) {
		return getAghAtendimentoDAO().listarAtendimentosPorNumeroConsultaOrdenado(
				consultaNumero);
	}
	
	
	@Override
	public List<AghAtendimentos> listarAtendimentosPorConsultaComInternacaoAtendumentoUrgenciaAtendumentoPacienteExternoNulo(
			Integer numeroConsulta) {
		return getAghAtendimentoDAO()
				.listarAtendimentosPorConsultaComInternacaoAtendumentoUrgenciaAtendumentoPacienteExternoNulo(
						numeroConsulta);
	}

	@Override
	public Long listarCaracteristicasEspecialidadesCount(Short espSeq,
			DominioCaracEspecialidade caracteristica) {
		return getAghCaractEspecialidadesDAO()
				.listarCaracteristicasEspecialidadesCount(espSeq,
						caracteristica);
	}

	protected AghCaractEspecialidadesDAO getAghCaractEspecialidadesDAO() {
		return aghCaractEspecialidadesDAO;
	}

	@Override
	public String obterCgcHospital() {

		String result = null;
		AghuConfigRN cfgRn = null;

		// V_CGC_HOSP := AGH_CONFIG.GET_CGC;
		// V_CGC_HOSP := replace(V_CGC_HOSP,'.','');
		// V_CGC_HOSP := replace(V_CGC_HOSP,'/','');
		// V_CGC_HOSP := replace(V_CGC_HOSP,'-','');
		cfgRn = this.getAghuConfigRN();
		result = cfgRn.getCgc();

		return result;
	}

	protected AghInstituicoesHospitalaresDAO getAghInstituicoesHospitalaresDAO() {

		return aghInstituicoesHospitalaresDAO;
	}

	@Override
	public List<AghInstituicoesHospitalares> listarInstHospPorIndLocalAtivo() {
		return getAghInstituicoesHospitalaresDAO().listarPorIndLocalAtivo();
	}

	@Override
	public List<ConstanteAghCaractUnidFuncionais> listarCaractUnidFuncionais(
			Short unfSeq, ConstanteAghCaractUnidFuncionais[] caracteristicas,
			Integer firstResult, Integer maxResults) {
		return this.getAghCaractUnidFuncionaisDAO().listarCaractUnidFuncionais(
				unfSeq, caracteristicas, firstResult, maxResults);
	}

	@Override
	public AghAtendimentos buscarAtendimentosPorCodigoInternacao(Integer intSeq) {
		return this.getAghAtendimentoDAO()
				.buscarAtendimentosPorCodigoInternacao(intSeq);
	}

	@Override
	public List<Date> listarDthrInicioAtendimentosPorCodigoInternacao(
			Integer intSeq, Integer firstResult, Integer maxResults) {
		return this.getAghAtendimentoDAO()
				.listarDthrInicioAtendimentosPorCodigoInternacao(intSeq,
						firstResult, maxResults);
	}

	@Override
	public AghImpressoraPadraoUnids obterImpressora(Short unfSeq,
			TipoDocumentoImpressao tipo) {
		return this.getAghImpressoraPadraoUnidsDAO().obterImpressora(unfSeq,
				tipo);
	}

	@Override
	public AghImpressoraPadraoUnids obterImpressora(Short unfSeq,
			String ipComputador, TipoDocumentoImpressao tipo) {
		return this.getAghImpressoraPadraoUnidsDAO().obterImpressora(unfSeq,
				ipComputador, tipo);
	}

	protected AghImpressoraPadraoUnidsDAO getAghImpressoraPadraoUnidsDAO() {
		return aghImpressoraPadraoUnidsDAO;
	}

	@Override
	@Secure("#{s:hasPermission('copiaContingencia','pesquisar')}")
	public List<AghDocumentoContingencia> pesquisarDocumentoContingenciaPorUsuarioNomeTipo(
			String login, String nomeDocumento, DominioMimeType tipo,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getAghDocumentoContingenciaDAO()
				.pesquisarDocumentoContingenciaPorUsuarioNomeTipo(login,
						nomeDocumento, tipo, firstResult, maxResult,
						orderProperty, asc);
	}

	private AghDocumentoContingenciaDAO getAghDocumentoContingenciaDAO() {
		return aghDocumentoContingenciaDAO;

	}

	@Override
	public Long obterCountDocumentoContingenciaPorUsuarioNomeTipo(
			String login, String nomeDocumento, DominioMimeType tipo) {
		return getAghDocumentoContingenciaDAO()
				.obterCountDocumentoContingenciaPorUsuarioNomeTipo(login,
						nomeDocumento, tipo);
	}

	@Override
	public List<AghEspecialidades> listarPorSigla(Object paramPesquisa) {
		return this.getEspecialidadeON().listarPorSigla(paramPesquisa);
	}

	@Override
	public boolean unidadeFuncionalPossuiCaracteristica(Short seq,
			ConstanteAghCaractUnidFuncionais... caracteristicas) {
		return getAghUnidadesFuncionaisDAO()
				.unidadeFuncionalPossuiCaracteristica(seq, caracteristicas);
	}

	@Override
	public List<AghUnidadesFuncionais> buscarSeqsUnidadesFuncionaisDisponivelLocalDispensacao(
			Short... unfsSolicitantesMdto) {
		return getAghUnidadesFuncionaisDAO()
				.buscarSeqsUnidadesFuncionaisDisponivelLocalDispensacao(
						unfsSolicitantesMdto);
	}

	@Override
	public Long buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(
			Short... idSUnfsSolicitantesMdto) {
		return getAghUnidadesFuncionaisDAO()
				.buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(
						idSUnfsSolicitantesMdto);
	}

	@Override
	public List<AghUnidadesFuncionais> buscarUnidadesFuncionaisDisponivelLocalDispensacao(
			Short[] idSUnfsSolicitantesMdto, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getAghUnidadesFuncionaisDAO()
				.buscarUnidadesFuncionaisDisponivelLocalDispensacao(
						idSUnfsSolicitantesMdto, firstResult, maxResult,
						orderProperty, asc);
	}

	@Override
	public List<AghUnidadesFuncionais> obterListaUnidadesFuncionaisAtivasPorCaracteristica(
			ConstanteAghCaractUnidFuncionais caracteristica) {
		return getAghCaractUnidFuncionaisDAO()
				.obterListaUnidadesFuncionaisAtivasPorCaracteristica(
						caracteristica);
	}

	@Override
	public AghAtendimentos obterAtendimento(Integer atdSeq,
			DominioPacAtendimento pacAtendimento,
			List<DominioOrigemAtendimento> origensAtendimento) {
		return getAghAtendimentoDAO().obterAtendimento(atdSeq, pacAtendimento,
				origensAtendimento);
	}

	@Override
	public String pegaHorarioDaUnidadeFuncional(
			AghUnidadesFuncionais unidadeFuncional) {
		return getAghUnidadesFuncionaisDAO().pegaHorarioDaUnidadeFuncional(
				unidadeFuncional);
	}
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentosPorItemSolicitacaoExame(
			Integer iseSoeSeq, Short iseSeqp, List<DominioOrigemAtendimento> origensAtendimento) {
		return this.getAghAtendimentoDAO().pesquisarAtendimentosPorItemSolicitacaoExame(iseSoeSeq, iseSeqp, origensAtendimento);
	}
	
	@Override
	public List<AghAtendimentos> obterAghAtendimentoPorDadosPaciente(Integer codigoPaciente, Integer prontuario) {
		return this.getAghAtendimentoDAO().obterAghAtendimentoPorDadosPaciente(codigoPaciente, prontuario);
	}
	
	@Override
	public AghAtendimentos obterAghAtendimentoPorSeq(Integer seq) {
		return this.getAghAtendimentoDAO().obterAghAtendimentoPorSeq(seq);
	}
	
	
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentosaPorUnidadeDataReferencia(
			AghUnidadesFuncionais unidadeFuncional, Date dataDeReferencia) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentosaPorUnidadeDataReferencia(
						unidadeFuncional, dataDeReferencia);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosAmbulatoriaisPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq,
			Integer qtdHorasPermitePrescricaoMedica){
		return getAghAtendimentoDAO().pesquisarAtendimentosAmbulatoriaisPrescricaoMedica(pacCodigo, atdSeq, qtdHorasPermitePrescricaoMedica);		
	}
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentosCirurgicosPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq,
			Integer qtdHorasPermitePrescricaoMedica){
		return getAghAtendimentoDAO().pesquisarAtendimentosCirurgicosPrescricaoMedica(pacCodigo, atdSeq, qtdHorasPermitePrescricaoMedica);		
	}
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentosExternoPrescricaoMedica(
			Integer pacCodigo, Integer atdSeq,
			Integer qtdHorasPermitePrescricaoMedica){
		return getAghAtendimentoDAO().pesquisarAtendimentosExternoPrescricaoMedica(pacCodigo, atdSeq, qtdHorasPermitePrescricaoMedica);	
	}
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentos(Integer pacCodigo, Integer atdSeq, 
			DominioPacAtendimento pacAtendimento, List<DominioOrigemAtendimento> origens) {
		return getAghAtendimentoDAO().pesquisarAtendimento(pacCodigo, atdSeq, pacAtendimento, origens);
	}
	
	@Override
	public List<AghAtendimentos> obterUnidadesAlta(Integer codigo,Date dataUltInternacao, Boolean buscarDtInt ) {
		return this.getAghAtendimentoDAO().obterUnidadesAlta(codigo, dataUltInternacao,buscarDtInt);
	}

	@Override
	public List<AghEspecialidades> listarEspecialidadesPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla) {
		return this.getEspecialidadeON()
				.listarEspecialidadesPorSiglaOuDescricao(paramPesquisa,
						ordemPorSigla);
	}

	@Override
	public List<AghEspecialidades> listarEspecialidadesPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla, boolean apenasAtivas) {
		return this.getEspecialidadeON()
				.listarEspecialidadesPorSiglaOuDescricao(paramPesquisa,
						ordemPorSigla, apenasAtivas);
	}

	@Override
	public List<AghEspecialidades> listarEspecialidadesSolicitacaoProntuario(
			Object paramPesquisa, boolean ordemPorSigla) {
		return this.getEspecialidadeON()
				.listarEspecialidadesSolicitacaoProntuario(paramPesquisa,
						ordemPorSigla);
	}

	@Override
	public Long listarEspecialidadesSolicitacaoProntuarioCount(
			Object paramPesquisa, boolean ordemPorSigla) {
		return this.getEspecialidadeON()
				.listarEspecialidadesSolicitacaoProntuarioCount(paramPesquisa,
						ordemPorSigla);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorNumeroConsulta(Integer pConNumero) {
		return this.getAghAtendimentoDAO().obterAtendimentoPorNumeroConsulta(
				pConNumero);
	}

	@Override
	public List<AghAtendimentos> pesquisaAtendimento(Integer prontuario,
			String leitoID) throws ApplicationBusinessException {
		return this.getAghAtendimentoDAO().pesquisaAtendimento(prontuario,
				leitoID);
	}

	protected AghProfEspecialidadesDAO getAghProfEspecialidadesDAO() {
		return aghProfEspecialidadesDAO;
	}

	/**
	 * @param matricula
	 * @param vinCodigo
	 * @return List<EspCrmVO>, contendo os dados do medico (CRM, nomeMedico,
	 *         nomeUsual, espSeq, cpf), onde cada elemento da lista armazena uma
	 *         especialidade do medico. (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#obterDadosDoMedicoPelaMatriculaEVinCodigo(java.lang.Integer,
	 *      java.lang.Short)
	 */
	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<EspCrmVO> obterDadosDoMedicoPelaMatriculaEVinCodigo(
			Integer matricula, Short vinCodigo) {
		return getAghProfEspecialidadesDAO()
				.obterDadosDoMedicoPelaMatriculaEVinCodigo(matricula, vinCodigo);
	}

	@Override
	public AghCaractUnidFuncionais obterAghCaractUnidFuncionais(
			AghCaractUnidFuncionaisId id) {
		return getAghCaractUnidFuncionaisDAO().obterPorChavePrimaria(id);
	}
	
	
	@Override	
	public boolean possuiCaracteristicaPorUnidadeEConstante(Short seqUnidadeFuncional, ConstanteAghCaractUnidFuncionais caracteristica){
		return getAghCaractUnidFuncionaisDAO().possuiCaracteristicaPorUnidadeEConstante(seqUnidadeFuncional, caracteristica);		
	}

	/**
	 * Metodo auxiliar para validacao de datas.
	 * 
	 * @param pacCodigo
	 * @return Date
	 * 
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getDataHoraFim(java.lang.Integer)
	 */
	@Override
	public Date getDataHoraFim(Integer pacCodigo) {
		return getAghAtendimentoDAO().getDataHoraFim(pacCodigo);
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getRazaoSocial()
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getRazaoSocial()
	 */
	@Override
	public String getRazaoSocial() {
		return this.getAghuConfigRN().getRazaoSocial();
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getEndereco()
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getEndereco()
	 */
	@Override
	public String getEndereco() {
		return this.getAghuConfigRN().getEndereco();
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getCidade()
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getCidade()
	 */
	@Override
	public String getCidade() {
		return this.getAghuConfigRN().getCidade();
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getCgc()
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getCgc()
	 */
	@Override
	public String getCgc() {
		return this.getAghuConfigRN().getCgc();
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getFax()
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getFax()
	 */
	@Override
	public String getFax() {
		return this.getAghuConfigRN().getFax();
	}

	/**
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#getUasg()
	 * @see br.gov.mec.aghu.business.AghuConfigRN#getUasg()
	 */
	@Override
	public Integer getUasg() {
		return this.getAghuConfigRN().getUasg();
	}

	@Override
	public List<AghProfEspecialidades> listarEspecialidadesPorServidor(
			RapServidores usuarioResponsavel) {
		return getAghProfEspecialidadesDAO().listarEspecialidadesPorServidor(
				usuarioResponsavel);
	}
	
	@Override
	public List<AghProfEspecialidades> listarProfEspecialidadesPorEspSeq(Short espSeq) {
		return this.getAghProfEspecialidadesDAO().listarProfEspecialidadesPorEspSeq(espSeq);
	}
	
	@Override
	public Integer getCcustAtuacao(RapServidores servidor) {
		return this.getAghuConfigRN().getCcustAtuacao(servidor);
	}

	/**
	 * Método resposável por buscar Instituicoes Hospitalares, conforme a string
	 * passada como parametro, que é comparada com o codigo e a nome da
	 * Instituicao Hospitalar É utilizado pelo converter
	 * AghInstituicoesHospitalaresConverter.
	 * 
	 * @param nome
	 *            ou codigo
	 * @return Lista de AghInstituicoesHospitalares (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#pesquisarInstituicaoHospitalarPorCodigoENome(java.lang.Object)
	 */
	@Override
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoENome(
			Object objPesquisa) {
		return getAghInstituicoesHospitalaresDAO()
				.pesquisarInstituicaoHospitalarPorCodigoENome(objPesquisa);
	}

	@Override
	public List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(
			Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio,
			Integer[] tiposQualificacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException {

		return getAghProfissionaisEspConvenioDAO()
				.pesquisarProfissionaisEscala(vinculo, matricula,
						conselhoProfissional, nomeServidor, siglaEspecialidade,
						codigoConvenio, descricaoConvenio, tiposQualificacao,
						firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarProfissionaisEscalaCount(Short vinculo,
			Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio,
			Integer[] tiposQualificacao) throws ApplicationBusinessException {
		return getAghProfissionaisEspConvenioDAO()
				.pesquisarProfissionaisEscalaCount(vinculo, matricula,
						conselhoProfissional, nomeServidor, siglaEspecialidade,
						codigoConvenio, descricaoConvenio, tiposQualificacao);
	}

	protected AghProfissionaisEspConvenioDAO getAghProfissionaisEspConvenioDAO() {
		return aghProfissionaisEspConvenioDAO;
	}

	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR c_gestacoes)
	 */
	@Override
	public AghAtendimentos obterAtendimentoGestacao(Integer intSeq) {
		return getAghAtendimentoDAO().obterAtendimentoGestacao(intSeq);
	}

	/**
	 * Pesquisa por atendimentos de gestações
	 */
	@Override
	public List<AghAtendimentos> pesquisarAtendimentosGestacoes(Integer intSeq) {
		return getAghAtendimentoDAO().pesquisarAtendimentosGestacoes(intSeq);
	}

	@Override
	public AghAtendimentos obterAtendimentoExames(Integer intSeq) {
		return getAghAtendimentoDAO().obterAtendimentoExames(intSeq);
	}

	/**
	 * Método que obtém o atendimento da internação
	 */
	@Override
	public AghAtendimentos obterAtendimentoInternacao(Integer intSeq) {
		return getAghAtendimentoDAO().obterAtendimentoInternacao(intSeq);
	}

	@Override
	public void removerAghAtendimentos(AghAtendimentos aghAtendimentos,
			boolean flush) {
		getAghAtendimentoDAO().remover(aghAtendimentos);
		if (flush) {
			getAghAtendimentoDAO().flush();
		}
	}

	@Override
	public AghAtendimentos atualizarAghAtendimentos(AghAtendimentos aghAtendimentos, boolean flush) {
		AghAtendimentos retorno = getAghAtendimentoDAO().merge(aghAtendimentos);
		if (flush) {
			getAghAtendimentoDAO().flush();
		}
		return retorno;
	}

	@Override
	public List<AghAtendimentoPacientes> pesquisarAghAtendimentoPacientesPorAtendimento(
			AghAtendimentos atendimento) {
		return getAghAtendimentoPacientesDAO()
				.pesquisarAghAtendimentoPacientesPorAtendimento(atendimento);
	}

	@Override
	public void removerAghAtendimentoPacientes(
			AghAtendimentoPacientes aghAtendimentoPacientes, boolean flush) {
		getAghAtendimentoPacientesDAO().remover(aghAtendimentoPacientes);
		if (flush) {
			getAghAtendimentoPacientesDAO().flush();
		}
	}

	protected AghAtendimentoPacientesDAO getAghAtendimentoPacientesDAO() {
		return aghAtendimentoPacientesDAO;
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosMaeGestacao(
			AghAtendimentos atendimentoGestacao) {
		return getAghAtendimentoDAO().pesquisarAtendimentosMaeGestacao(
				atendimentoGestacao);
	}

	protected AghOrigemEventosDAO getAghOrigemEventosDAO() {
		return aghOrigemEventosDAO;
	}

	@Override
	public AghOrigemEventos obterAghOrigemEventosPorChavePrimaria(Short seq) {
		return getAghOrigemEventosDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public AghInstituicoesHospitalares obterAghInstituicoesHospitalaresPorChavePrimaria(
			Integer seq) {
		return getAghInstituicoesHospitalaresDAO().obterPorChavePrimaria(seq);
	}

	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query antes do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 * 
	 * @dbtables AghEspecialidades select
	 * @dbtables AghProfEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * 
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#obterCriteriaProfessoresInternacaoUnion1(java.lang.String,
	 *      java.lang.Integer, java.lang.Short)
	 */
	@Override
	public List<Object[]> obterCriteriaProfessoresInternacaoUnion1(
			String strPesquisa, Integer matriculaProfessor,
			Short vinCodigoProfessor) {
		return getAghProfEspecialidadesDAO()
				.obterCriteriaProfessoresInternacaoUnion1(strPesquisa,
						matriculaProfessor, vinCodigoProfessor);
	}

	@Override
	public List<Object[]> obterProfessoresInternacao(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor) {
		return getAghProfEspecialidadesDAO().obterProfessoresInternacao(
				strPesquisa, matriculaProfessor, vinCodigoProfessor);
	}
	
	@Override
	public Long obterProfessoresInternacaoCount(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor) {
		return getAghProfEspecialidadesDAO().obterProfessoresInternacaoCount(
				strPesquisa, matriculaProfessor, vinCodigoProfessor);
	}
	
	@Override
	public List<Object[]> obterProfessoresInternacaoTodos(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor) {
		return getAghProfEspecialidadesDAO().obterProfessoresInternacaoTodos(
				strPesquisa, matriculaProfessor, vinCodigoProfessor);
	}
	
	@Override
	public Long obterProfessoresInternacaoTodosCount(String strPesquisa,
			Integer matriculaProfessor, Short vinCodigoProfessor) {
		return getAghProfEspecialidadesDAO().obterProfessoresInternacaoTodosCount(
				strPesquisa, matriculaProfessor, vinCodigoProfessor);
	}

	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query depois do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#obterCriteriaProfessoresInternacaoUnion2(java.lang.String,
	 *      java.lang.Integer, java.lang.Short)
	 */
	@Override
	public List<Object[]> obterCriteriaProfessoresInternacaoUnion2(
			String strPesquisa, Integer matriculaProfessor,
			Short vinCodigoProfessor) {
		return getAghProfEspecialidadesDAO()
				.obterCriteriaProfessoresInternacaoUnion2(strPesquisa,
						matriculaProfessor, vinCodigoProfessor);
	}
	@Override
	public AghProfEspecialidades obterAghProfEspecialidadesPorChavePrimaria(
			AghProfEspecialidadesId id) {
		return getAghProfEspecialidadesDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public AghProfEspecialidades obterProfEspecialidadeComServidorAtivoProgramado(AghProfEspecialidadesId id) {
		return this.getAghProfEspecialidadesDAO().obterProfEspecialidadeComServidorAtivoProgramado(id);
	}

	@Override
	public AghHorariosUnidFuncional inserirAghHorariosUnidFuncional(
			AghHorariosUnidFuncional unidFunc, boolean flush) {
		getAghHorariosUnidFuncionalDAO().persistir(unidFunc);
		if (flush) {
			getAghHorariosUnidFuncionalDAO().flush();
		}
		return unidFunc;
	}

	@Override
	public void removerAghHorariosUnidFuncional(AghHorariosUnidFuncionalId id) {
		aghHorariosUnidFuncionalDAO.removerPorId(id);
	}

	@Override
	public AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPorId(
			AghUnidadesFuncionais unidadeFuncional, DominioTipoDia tipoDia,
			String horaProgramada) {
		return getAghHorariosUnidFuncionalDAO()
				.obterHorarioUnidadeFuncionalPorId(unidadeFuncional, tipoDia,
						horaProgramada);
	}

	@Override
	public AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPorId(AghHorariosUnidFuncionalId id, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return getAghHorariosUnidFuncionalDAO().obterPorChavePrimaria(id, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}

	private AghHorariosUnidFuncionalDAO getAghHorariosUnidFuncionalDAO() {
		return aghHorariosUnidFuncionalDAO;
	}

	@Override
	public List<AghCid> pesquisarSubCid(String codigoCid) {
		return getAghCidsDAO().pesquisarSubCid(codigoCid);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosGestacoesEmAtendimento(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentosGestacoesEmAtendimento(gsoPacCodigo,
						gsoSeqp);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosGestacoesFinalizados(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentosGestacoesFinalizados(gsoPacCodigo,
						gsoSeqp);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosEmAtendimentoPeloAtendimentoMae(
			AghAtendimentos atendimentoMae) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentosEmAtendimentoPeloAtendimentoMae(
						atendimentoMae);
	}

	@Override
	public List<AghCaractUnidFuncionais> listaCaracteristicasUnidadesFuncionaisPaciente(
			Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return getAghCaractUnidFuncionaisDAO()
				.listaCaracteristicasUnidadesFuncionaisPaciente(unfSeq,
						caracteristica);
	}

	@Override
	public List<Object[]> pesquisarDadosPacienteAtendimento(Integer codPaciente) {
		return this.getAghAtendimentoDAO().pesquisarDadosPacienteAtendimento(
				codPaciente);
	}

	@Override
	public Long pesquisaPorCidCount(Integer seq, String codigo,
			String descricao, DominioSituacao situacaoPesquisa) {
		return this.getAghCidsDAO().pesquisaPorCidCount(seq, codigo, descricao,
				situacaoPesquisa);
	}

	@Override
	public List<AghCid> pesquisaPorCid(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer seq, String codigo,
			String descricao, DominioSituacao situacaoPesquisa) {
		return this.getAghCidsDAO().pesquisaPorCid(firstResult, maxResults,
				orderProperty, asc, seq, codigo, descricao, situacaoPesquisa);
	}

	@Override
	public List<AghCid> pesquisarCidsSemSubCategoriaPorCodigoDescricaoOuId(
			String descricao, Integer limiteRegistros) {
		return this.getAghCidsDAO()
				.pesquisarCidsSemSubCategoriaPorCodigoDescricaoOuId(descricao,
						limiteRegistros);
	}

	@Override
	public List<AghCid> pesquisarCidsComSubCategoriaCodigoPorDescricaoOuId(
			String descricao, Integer limiteRegistros) {
		return this.getAghCidsDAO()
				.pesquisarCidsComSubCategoriaCodigoPorDescricaoOuId(descricao,
						limiteRegistros);
	}
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarEspecialidadePorSiglaENome(
			Object paramPesquisa) {
		return this.getEspecialidadeON().listarEspecialidadePorSiglaENome(
				paramPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarEspecialidadeAtivasNaoInternas(
			Object paramPesquisa) {
		return this.getEspecialidadeON().listarEspecialidadeAtivasNaoInternas(
				paramPesquisa);
	}

	/**
	 * Lista especialidades ATIVAS pela siga ou descricao ordenando os
	 * resultados pela sigla ou descricao.
	 * 
	 * @param paramPesquisa
	 *            - sigla ou descricao
	 * @return (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#listarEspecialidadesAtivasPorSiglaOuDescricao(java.lang.Object,
	 *      boolean)
	 */
	@Override
	public List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla) {
		return this.getEspecialidadeON()
				.listarEspecialidadesAtivasPorSiglaOuDescricao(paramPesquisa,
						ordemPorSigla);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarPermitemConsultoriaPorSigla(
			String paramPesquisa, boolean apenasSiglaSePossivel) {
		return this.getEspecialidadeON().listarPermitemConsultoriaPorSigla(
				paramPesquisa, apenasSiglaSePossivel);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarEspecialidadeTransPaciente(
			Object paramPesquisa, Integer idade) {
		return this.getEspecialidadeON().listarEspecialidadeTransPaciente(
				paramPesquisa, idade);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarPorSiglaAtivas(Object paramPesquisa) {
		return this.getEspecialidadeON().listarPorSiglaAtivas(paramPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarEspecialidadeAtualizaSolicitacaoInternacao(
			Object paramPesquisa, Integer idade) {
		return this.getEspecialidadeON()
				.listarEspecialidadeAtualizaSolicitacaoInternacao(
						paramPesquisa, idade);
	}

	/**
	 * Lista apenas as especialidades ativas que permitem consultoria
	 * pesquisando por sigla e descrição.
	 * 
	 * @param paramPesquisa
	 * @return (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#listarPermitemConsultoriaPorSigla(java.lang.Object)
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarPermitemConsultoriaPorSigla(
			Object paramPesquisa) {
		return this.getEspecialidadeON().listarPermitemConsultoriaPorSigla(
				paramPesquisa);
	}

	protected AghTabelasSistemaDAO getAghTabelasSistemaDAO() {
		return aghTabelasSistemaDAO;
	}

	@Override
	public List<AghTabelasSistema> pesquisarTabelasSistema(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String nome, AghCoresTabelasSistema cor,
			DominioCategoriaTabela categoria, String versao, String origem) {
		return getAghTabelasSistemaDAO().pesquisarTabelasSistema(firstResult,
				maxResult, orderProperty, asc, seq, nome, cor, categoria,
				versao, origem);
	}

	@Override
	public Long pesquisarTabelasSistemaCount(Integer seq, String nome,
			AghCoresTabelasSistema cor, DominioCategoriaTabela categoria,
			String versao, String origem) {
		return getAghTabelasSistemaDAO().pesquisarTabelasSistemaCount(seq,
				nome, cor, categoria, versao, origem);
	}

	@Override
	public AghTabelasSistema obterTabelaSistema(Integer seq) {
		return getAghTabelasSistemaDAO().obterTabelaSistemaPeloId(seq);
	}

	@Override
	public AghTabelasSistema obterTabelaSistemaPeloId(Integer seq) {
		return getAghTabelasSistemaDAO().obterPorChavePrimaria(seq, null, new Enum[] { AghTabelasSistema.Fields.COR, AghTabelasSistema.Fields.SERVIDOR_RESPONSAVEL});
	}

	@Override
	public void removerTabelaSistema(AghTabelasSistema tabelaSistema)
			throws BaseException {
		getTabelasSistemaCRUD().removerTabelaSistema(tabelaSistema);
	}

	@Override
	public void persistirTabelaSistema(AghTabelasSistema tabelaSistema)
			throws BaseException {
		getTabelasSistemaCRUD().persistirTabelaSistema(tabelaSistema);
	}

	public TabelasSistemaCRUD getTabelasSistemaCRUD() {
		return tabelasSistemaCRUD;
	}

	@Override
	public List<AghCoresTabelasSistema> pesquisarCoresTabelasSistema(
			Object parametro) {
		return this.getAghCoresTabelasSistemaDAO()
				.pesquisarCoresTabelasSistema(parametro);
	}

	private AghCoresTabelasSistemaDAO getAghCoresTabelasSistemaDAO() {
		return aghCoresTabelasSistemaDAO;
	}


	private RemoverLogsAplicacaoSchedulerRN getRemoverLogsAplicacaoSchedulerRN() {
		return removerLogsAplicacaoSchedulerRN;
	}

	@Override
	public void removerLogsAplicacao(Date expiration, String cron) throws ApplicationBusinessException {
		this.getRemoverLogsAplicacaoSchedulerRN().removerLogsAplicacao();
	}

	/**
	 * Busca log da aplicação de acordo com o filtro informado.
	 */
	@Override
	public List<AghLogAplicacao> pesquisarAghLogAplicacao(String usuario,
			Date dthrCriacaoIni, Date dthrCriacaoFim, String classe,
			String nivel, String mensagem, Integer firstResult,
			Integer maxResult) {

		AghLogAplicacaoON aghLogAplicacaoON = this.getAghLogAplicacaoON();

		return aghLogAplicacaoON.pesquisarAghLogAplicacao(usuario,
				dthrCriacaoIni, dthrCriacaoFim, classe, nivel, mensagem,
				firstResult, maxResult);
	}

	/**
	 * Busca número de registros de log da aplicação de acordo com o filtro
	 * informado.
	 */
	@Override
	public Long pesquisarAghLogAplicacaoCount(String usuario,
			Date dthrCriacaoIni, Date dthrCriacaoFim, String classe,
			String nivel, String mensagem) {

		AghLogAplicacaoON aghLogAplicacaoON = this.getAghLogAplicacaoON();

		return aghLogAplicacaoON.pesquisarAghLogAplicacaoCount(usuario,dthrCriacaoIni, dthrCriacaoFim, classe, nivel, mensagem);
	}

	protected AghLogAplicacaoON getAghLogAplicacaoON() {
		return aghLogAplicacaoON;
	}

	/**
	 * Obtém os idf das unidades funcionas que respeitam as devidas
	 * características passados por parametro
	 */
	private List<Short> getIdfUnfCaracteristicas(
			ConstanteAghCaractUnidFuncionais[] listaCaracteristicas) {
		List<ConstanteAghCaractUnidFuncionais> caracteristicasTemp = new ArrayList<ConstanteAghCaractUnidFuncionais>();
		for (ConstanteAghCaractUnidFuncionais carac : listaCaracteristicas) {
			caracteristicasTemp.add(carac);
		}

		// Obtém as unidades que respeitam as devidas características
		List<Short> listaUnfSeq = getAghCaractUnidFuncionaisDAO()
				.pesquisarUnfSeqsPorCaracteristicasRelatorioPrescricaoPorUnidade(
						caracteristicasTemp);
		return listaUnfSeq;
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosInternacao(
			Integer numeroProntuario, Date date, Short vlrOrigem) {
		return getAghAtendimentoDAO().pesquisarAtendimentosInternacao(
				numeroProntuario, date, vlrOrigem);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosInternacaoEmergencia(
			Integer numeroProntuario, Date date, Short vlrOrigem) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentosInternacaoEmergencia(numeroProntuario,
						date, vlrOrigem);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosSO(
			Integer numeroProntuario, Date date) {
		return getAghAtendimentoDAO().pesquisarAtendimentosSO(numeroProntuario,
				date);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosConsultas(
			Integer numeroProntuario, Date date) {
		return getAghAtendimentoDAO().pesquisarAtendimentosConsultas(
				numeroProntuario, date);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosCirurgias(
			Integer numeroProntuario, Date date) {
		return getAghAtendimentoDAO().pesquisarAtendimentosCirurgias(
				numeroProntuario, date);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosPacExterno(
			Integer numeroProntuario, Date date) {
		return getAghAtendimentoDAO().pesquisarAtendimentosPacExterno(
				numeroProntuario, date);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosNascimentos(
			Integer numeroProntuario, Date date) {
		return getAghAtendimentoDAO().pesquisarAtendimentosNascimentos(
				numeroProntuario, date);
	}

	@Override
	public List<AtendimentosVO> pesquisarAtendimentosNeonatologia(
			Integer numeroProntuario, Date date) {
		return getAghAtendimentoDAO().pesquisarAtendimentosNeonatologia(
				numeroProntuario, date);
	}

	@Override
	public String buscarFuncaoCracha(final Integer matricula,
			final Short codigoVinculo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return getObjetosOracleDAO().buscarFuncaoCracha(matricula,
				codigoVinculo, servidorLogado);
	}

	/**
	 * Verifica se é o banco de produção
	 * 
	 * @return Verdadeiro se for o banco de produção do HCPA (non-Javadoc)
	 * @see br.gov.mec.aghu.business.IAghuFacadeTemp#isProducaoHCPA()
	 */
	@Override
	public Boolean isProducaoHCPA() {
		return getObjetosOracleDAO().isProducaoHCPA();
	}

	@Override
	public String buscarDescricaoOcupacao(Integer codigoOcupacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return getObjetosOracleDAO().buscarDescricaoOcupacao(codigoOcupacao,
				servidorLogado);
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarPorDescricaoCodigoAtivaAssociada(
			String paramString) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarPorDescricaoCodigoAtivaAssociada(paramString);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica(final String filtro, final ConstanteAghCaractUnidFuncionais[] caracteristicasUnideFuncional, final DominioSituacao situacao, Boolean order) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristica(filtro, caracteristicasUnideFuncional, situacao, order);
	}

	@Override
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristicaCount(final String filtro, final ConstanteAghCaractUnidFuncionais[] caracteristicasUnideFuncional, final DominioSituacao situacao, Boolean order) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisPorCodigoDescricaoCaracteristicaCount(filtro, caracteristicasUnideFuncional, situacao, order);
	}

	@Override
	public List<PacienteInternadoVO> listarControlePacientesInternados(
			RapServidores servidor) {
		return this.getAghAtendimentoDAO().listarControlePacientesInternados(
				servidor);
	}

	@Override
	public AghAtendimentos obterAtendimentoVigente(AipPacientes paciente) {
		return getAghAtendimentoDAO().obterAtendimentoVigente(paciente);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoVigenteDetalhado(AipPacientes paciente) {
		return getAghAtendimentoDAO().obterAtendimentoVigenteDetalhado(paciente);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoVigente(AinLeitos leito) {
		return getAghAtendimentoDAO().pesquisarAtendimentoVigente(leito);
	}

	@Override
	public List<Object[]> listarAtendimentosPacienteTratamentoPorCodigo(
			Integer pacCodigo, Integer pTipoTratamento) {
		return this.getAghAtendimentoDAO()
				.listarAtendimentosPacienteTratamentoPorCodigo(pacCodigo,
						pTipoTratamento);
	}

	@Override
	public List<Object[]> listarAtendimentosPacienteEmAndamentoPorCodigo(
			Integer pacCodigo) {
		return this.getAghAtendimentoDAO()
				.listarAtendimentosPacienteEmAndamentoPorCodigo(pacCodigo);
	}

	@Override
	public List<ImpImpressora> listarImpImpressorasPorTipoDocumentoImpressao(
			TipoDocumentoImpressao tipoImpressora) {
		return this.getAghImpressoraPadraoUnidsDAO()
				.listarImpImpressorasPorTipoDocumentoImpressao(tipoImpressora);
	}

	@Override
	public boolean existeAtendimentoComSolicitacaoExame(
			final AacConsultas consulta) {
		return getAghAtendimentoDAO().existeAtendimentoComSolicitacaoExame(
				consulta);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosPorNumeroConsulta(
			final Integer numeroConsulta) {
		return getAghAtendimentoDAO().pesquisarAtendimentosPorNumeroConsulta(
				numeroConsulta);
	}

	@Override
	public AghAtendimentos obterAtendimentoContrEscCirurg(Integer aipPacientesCodigo){
		return getAghAtendimentoDAO().obterAtendimentoContrEscCirurg(aipPacientesCodigo);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorConsulta(
			final Integer consultaNumero) {
		return getAghAtendimentoDAO().listarAtendimentosPorConsulta(
				consultaNumero);
	}

	@Override
	public AghCaractEspecialidades obterCaracteristicaEspecialidadePorChavePrimaria(
			AghCaractEspecialidadesId caractEspecialidadesId) {
		return getAghCaractEspecialidadesDAO().obterPorChavePrimaria(
				caractEspecialidadesId);
	}

	@Override
	public List<AghFeriados> pesquisarFeriadoPaginado(Date data,
			DominioMesFeriado mes, Integer ano, DominioTurno turno,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.getAghFeriadosDAO().pesquisarFeriadoPaginado(data, mes,
				ano, turno, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long countFeriado(Date data, DominioMesFeriado mes, Integer ano,
			DominioTurno turno) {
		return this.getAghFeriadosDAO().countPesquisaFeriado(data, mes, ano,
				turno);
	}

	@Override
	public AghFeriados obterFeriado(Date data) {
		return this.getAghFeriadosDAO().obterFeriado(data);
	}

	@Override
	public void removerFeriado(Date idExclusao)
			throws ApplicationBusinessException {
		this.getManterFeriadoRN().remover(idExclusao);
	}

	@Override
	public void persistirFeriado(AghFeriados feriado,
			DominioOperacoesJournal operacao) throws BaseException {
		this.getManterFeriadoRN().persistirFeriado(feriado, operacao);
	}

	@Override
	public List<AghProfEspecialidades> listaProfEspecialidades(
			RapServidores servidor, Short espSeq) {
		return getAghProfEspecialidadesDAO().listaProfEspecialidades(servidor,
				espSeq);
	}
	
	@Override
	public boolean verificarExisteProfEspecialidadePorServidorEspSeq(RapServidores servidor, Short espSeq) {
		return this.aghProfEspecialidadesDAO.verificarExisteProfEspecialidadePorServidorEspSeq(servidor, espSeq);
	}

	@Override
	public void gerarCheckOut(final Integer seq, final Integer pacCodigo,
			final String tipoAltaMedicaCodigoOld,
			final String tipoAltaMedicaCodigo, final Short unfSeqOld,
			final Short unfSeq, final Boolean pacienteInternadoOld,
			final Boolean pacienteInternacao)
			throws ObjetosOracleException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().gerarCheckOut(seq, pacCodigo,
				tipoAltaMedicaCodigoOld, tipoAltaMedicaCodigo, unfSeqOld,
				unfSeq, pacienteInternadoOld, pacienteInternacao,
				servidorLogado);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorNumeroConsultaLeitoProntuario(
			Integer pProntuario, Integer pConNumero, String leitoID) {
		return getAghAtendimentoDAO()
				.obterAtendimentoPorNumeroConsultaLeitoProntuario(pProntuario,
						pConNumero, leitoID);
	}

	@Override
	public void atualizarSituacaoTriagem(final Integer pacCodigo)
			throws ObjetosOracleException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().atualizarSituacaoTriagem(pacCodigo, servidorLogado);
	}

	@Override
	public void mpmkPprRnRnPprpDelUopProt(final Integer atdSeq, final Long seq)
			throws ObjetosOracleException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getObjetosOracleDAO().mpmkPprRnRnPprpDelUopProt(atdSeq, seq, servidorLogado);
	}

	@Override
	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidadePorEspecialidade(
			Short espSeq) {
		return getAghCaractEspecialidadesDAO()
				.pesquisarCaracteristicaEspecialidadePorEspecialidade(espSeq);
	}

	@Override
	public List<AghEspecialidades> getListaEspecialidadesServico(
			String parametro, FccCentroCustos servico) {
		return getAghEspecialidadesDAO().pesquisarPorNomeOuSiglaAmbulatorio(
				parametro, servico);
	}

	protected AghFeriadosDAO getAghFeriadosDAO() {
		return aghFeriadosDAO;
	}

	protected ManterFeriadoRN getManterFeriadoRN() {
		return manterFeriadoRN;
	}

	@Override
	public List<Object[]> pesquisaInformacoesUnidadesFuncionaisEscalaCirurgias(
			final Short seq) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisaInformacoesUnidadesFuncionaisEscalaCirurgias(seq);
	}

	@Override
	public Long obterQtdFeriadosEntreDatasSemFindeSemTurno(Date inicio,
			Date fim) {
		return getAghFeriadosDAO().obterQtdFeriadosEntreDatasSemFindeSemTurno(
				inicio, fim);
	}

	@Override
	public List<AghEspecialidades> getListaEspecialidades(String parametro) {
		return getAghEspecialidadeON().getListaEspecialidades(parametro);
	}

	@Override
	public List<AghEspecialidades> getListaEspecialidadesTodasSituacoes(
			String parametro) {
		return getAghEspecialidadeON().getListaEspecialidadesTodasSituacoes(
				parametro);
	}

	@Override
	public List<AghEspecialidades> getListaTodasEspecialidades(String parametro) {
		return getAghEspecialidadeON().getListaTodasEspecialidades(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidades(String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeOuSigla(parametro);
	}
	
	@Override
	public Long pesquisarEspecialidadesCount(String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeOuSiglaCount(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesAtivas(
			String parametro) {
		return getAghEspecialidadeON().pesquisarEspecialidadesAtivas(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasOrigemSumario(
			String parametro, Integer atdSeq, RapServidores servidorLogado) {
		return getAghEspecialidadeON().pesquisarEspecialidadesAtivasOrigemSumario(parametro, atdSeq, servidorLogado);
	}

	@Override
	public List<AghProfEspecialidades> listaProfEspecialidadesEquipe(
			AghEspecialidades especialidade, RapServidores servidorEquipe) {
		return this.getAghProfEspecialidadesDAO()
				.listaProfEspecialidadesEquipe(especialidade, servidorEquipe);
	}

	protected AghEspecialidadeON getAghEspecialidadeON() {
		return aghEspecialidadeON;
	}

	@Override
	public List executarCursorAtdMae(Integer atqSeqMae) {
		return this.getAghAtendimentoDAO().executarCursorAtdMae(atqSeqMae);
	}

	@Override
	public Integer executarCursorAtdPac(Integer atdSeq) {
		return this.getAghAtendimentoDAO().executarCursorAtdPac(atdSeq);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentos(Integer pacCodigo,
			Byte seqTipoTratamento) {
		return this.getAghAtendimentoDAO().listarAtendimentos(pacCodigo,
				seqTipoTratamento);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorConsultaEOrigem(
			DominioOrigemAtendimento dominioOrigemAtendimento,
			Integer consultaNumero) {
		return this.getAghAtendimentoDAO()
				.listarAtendimentosPorConsultaEOrigem(dominioOrigemAtendimento,
						consultaNumero);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorMcoGestacoes(
			Integer gsoPacCodigo, Short gsoSeqp) {
		return this.getAghAtendimentoDAO().listarAtendimentosPorMcoGestacoes(
				gsoPacCodigo, gsoSeqp);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorSeqAtendimentoUrgencia(
			Integer seqAtendimentoUrgencia) {
		return this.getAghAtendimentoDAO()
				.listarAtendimentosPorSeqAtendimentoUrgencia(
						seqAtendimentoUrgencia);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorSeqHospitalDia(
			Integer seqHospitalDia) {
		return this.getAghAtendimentoDAO().listarAtendimentosPorSeqHospitalDia(
				seqHospitalDia);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorSeqInternacao(
			Integer seqInternacao) {
		return this.getAghAtendimentoDAO().listarAtendimentosPorSeqInternacao(
				seqInternacao);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoNacimentoEmAndamento(
			AipPacientes paciente) {
		return this.getAghAtendimentoDAO()
				.obterAtendimentoNacimentoEmAndamento(paciente);
	}

	@Override
	public AghAtendimentos obterAtendimentoPeloSeq(Integer atdSeq) {
		return this.getAghAtendimentoDAO().obterAtendimentoPeloSeq(atdSeq);
	}

	@Override
	public AghAtendimentos obterAghAtendimentosComInternacaoEAtendimentoUrgencia(final Integer atdSeq) {
		return this.getAghAtendimentoDAO().obterAghAtendimentosComInternacaoEAtendimentoUrgencia(atdSeq);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorProntuarioPacienteAtendimento(
			Integer prontuario) {
		return this.getAghAtendimentoDAO()
				.obterAtendimentoPorProntuarioPacienteAtendimento(prontuario);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorSeqHospitalDia(
			Integer seqHospitalDia) {
		return this.getAghAtendimentoDAO().obterAtendimentoPorSeqHospitalDia(
				seqHospitalDia);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorSeqInternacao(
			Integer seqInternacao) {
		return this.getAghAtendimentoDAO().obterAtendimentoPorSeqInternacao(
				seqInternacao);
	}

	@Override
	public AghAtendimentos obterAtendimentoEmAndamento(Integer seq) {
		return this.getAghAtendimentoDAO().obterAtendimentoEmAndamento(seq);
	}
	
	@Override
	public List<AghAtendimentos> obterAtendimentosPorPaciente(Integer codigoPaciente, Integer atdSeq,
			DominioPacAtendimento indPacAtendimento, ConstanteAghCaractUnidFuncionais ... caracteristicas) {
		return this.getAghAtendimentoDAO().obterAtendimentosPorPaciente(
				codigoPaciente,atdSeq, indPacAtendimento, caracteristicas);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorPaciente(Integer codigoPaciente,
			DominioPacAtendimento indPacAtendimento, String caracteristica) {
		return this.getAghAtendimentoDAO().obterAtendimentoPorPaciente(
				codigoPaciente, indPacAtendimento, caracteristica);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentoPorSeqAtendimentoUrgencia(
			Integer seqAtendimentoUrgencia) {
		return this.getAghAtendimentoDAO()
				.obterAtendimentoPorSeqAtendimentoUrgencia(
						seqAtendimentoUrgencia);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentosRecemNascidosPorProntuarioMae(
			AghAtendimentos aghAtendimentos) {
		return this
				.getAghAtendimentoDAO()
				.obterAtendimentosRecemNascidosPorProntuarioMae(aghAtendimentos);
	}

	@Override
	public AghAtendimentos obterUltimoAtendimento(Integer aipPacientesCodigo) {
		return this.getAghAtendimentoDAO().obterUltimoAtendimento(
				aipPacientesCodigo);
	}

	@Override
	public AghAtendimentos obterUltimoAtendimentoGestacao(
			AipPacientes pacienteMae) {
		return this.getAghAtendimentoDAO().obterUltimoAtendimentoGestacao(
				pacienteMae);
	}

	@Override
	public List<AghAtendimentos> pesquisarAghAtendimentos(Integer pacCodigo,
			Boolean origemCirurgia) {
		return this.getAghAtendimentoDAO().pesquisarAghAtendimentos(pacCodigo,
				origemCirurgia);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoPacienteTipoAtendimento(
			Integer codigoPaciente) {
		return this.getAghAtendimentoDAO()
				.pesquisarAtendimentoPacienteTipoAtendimento(codigoPaciente);
	}

	@Override
	public List<AtendimentosEmergenciaPOLVO> pesquisarAtendimentosEmergenciaPOL(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer codigo, Date dthrInicio, Date dthrFim,
			Integer numeroConsulta, Short seqEspecialidade) {
		return this.getAghAtendimentoDAO().pesquisarAtendimentosEmergenciaPOL(
				firstResult, maxResult, orderProperty, asc, codigo, dthrInicio,
				dthrFim, numeroConsulta, seqEspecialidade);
	}

	@Override
	public Long pesquisarAtendimentosEmergenciaPOLCount(Integer codigo,
			Date dthrInicio, Date dthrFim, Integer numeroConsulta,
			Short seqEspecialidade) {
		return this.getAghAtendimentoDAO()
				.pesquisarAtendimentosEmergenciaPOLCount(codigo, dthrInicio,
						dthrFim, numeroConsulta, seqEspecialidade);
	}

	@Override
	public AghHorariosUnidFuncional obterHorarioUnidadeFuncionalPor(
			AghUnidadesFuncionais unfExecutora, DominioTipoDia tipoDia,
			Date dataHoraProgramada) {
		return getAghHorariosUnidFuncionalDAO()
				.obterHorarioUnidadeFuncionalPor(unfExecutora, tipoDia,
						dataHoraProgramada);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosPorInternacao(
			Integer seqInternacao) {
		return this.getAghAtendimentoDAO().pesquisarAtendimentosPorInternacao(
				seqInternacao);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosPorPaciente(
			Integer codigoPaciente) {
		return this.getAghAtendimentoDAO().pesquisarAtendimentosPorPaciente(
				codigoPaciente);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosPorPacienteGestacao(
			Integer codigoPacienteGestacao, Short seqGestacao) {
		return this.getAghAtendimentoDAO()
				.pesquisarAtendimentosPorPacienteGestacao(
						codigoPacienteGestacao, seqGestacao);
	}

	@Override
	public List<AghAtendimentos> pesquisarTipoAtendimento(
			Integer codigoPaciente,
			DominioTipoTratamentoAtendimento tipoTratamento, Date dataInicio,
			Date dataFim) {
		return this.getAghAtendimentoDAO().pesquisarTipoAtendimento(
				codigoPaciente, tipoTratamento, dataInicio, dataFim);
	}

	@Override
	public Integer recuperarCodigoPaciente(Integer altanAtdSeq)
			throws ApplicationBusinessException {
		return this.getAghAtendimentoDAO().recuperarCodigoPaciente(altanAtdSeq);
	}

	@Override
	public void removerAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.getAghAtendimentoDAO().remover(aghAtendimentos);
	}

	@Override
	public void inserirAghAtendimentos(AghAtendimentos aghAtendimentos,
			boolean flush) {
		this.getAghAtendimentoDAO().persistir(aghAtendimentos);
		if (flush) {
			this.getAghAtendimentoDAO().flush();
		}
	}

	@Override
	public List<AghNodoPol> recuperarAghNodoPolPorOrdem() {
		return this.getAghNodoPolDAO().recuperarAghNodoPolPorOrdem();
	}
	
	@Override
	public List<AghNodoPol> recuperarAghNodoPolPorOrdem(String [] tipos) {
		return this.getAghNodoPolDAO().recuperarAghNodoPolPorOrdem(tipos);
	}

	@Override
	public List<AghEspecialidades> pesquisarSolicitarProntuarioEspecialidade(
			String strPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarSolicitarProntuarioEspecialidade(strPesquisa);
	}

	@Override
	public List<AghDocumentosAssinados> listarDocAssPorSeqCirurgiaDocAssinadoIsNotNull(
			Integer crgSeq) {
		return this.getAghDocumentosAssinadosDAO()
				.listarDocAssPorSeqCirurgiaDocAssinadoIsNotNull(crgSeq);
	}

	@Override
	public AghAtendimentoPacientes obterAtendimentoPaciente(
			Integer altanAtdSeq, Integer apaSeq) throws ApplicationBusinessException {
		return this.getAghAtendimentoPacientesDAO().obterAtendimentoPaciente(
				altanAtdSeq, apaSeq);
	}

	@Override
	public Integer obterValorSequencialIdAghAtendimentoPacientes() {
		return this.getAghAtendimentoPacientesDAO().obterValorSequencialId();
	}

	@Override
	public void inserirAghAtendimentoPacientes(
			AghAtendimentoPacientes aghAtendimentoPacientes) {
		this.getAghAtendimentoPacientesDAO().persistir(aghAtendimentoPacientes);
		this.getAghAtendimentoPacientesDAO().flush();
	}

	@Override
	public Integer recuperarAtendimentoPaciente(Integer altanAtdSeq)
			throws ApplicationBusinessException {
		return this.getAghAtendimentoPacientesDAO()
				.recuperarAtendimentoPaciente(altanAtdSeq);
	}

	@Override
	public boolean verificarAghAtendimentoPacientes(Integer altanAtdSeq,
			Integer apaSeq) throws ApplicationBusinessException {
		return this.getAghAtendimentoPacientesDAO()
				.verificarAghAtendimentoPacientes(altanAtdSeq, apaSeq);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List listarLocalDataUltExamePorHistoricoPacienteVO(
			HistoricoPacienteVO historicoVO) {
		return this.getAghUnidadesFuncionaisDAO()
				.listarLocalDataUltExamePorHistoricoPacienteVO(historicoVO);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricao(
			String filtro) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricao(filtro);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
			Object filtro, Object[] caracteristicas) {
		return this
				.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoComFiltroPorCaractr(
						filtro, caracteristicas);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas) {
		return this
				.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalComUnfSeqPorCodigoDescricaoComFiltroPorCaract(
						objPesquisa, ordernarPorCodigoAlaDescricao,
						apenasAtivos, caracteristicas);
	}

	@Override
	public List<AghFeriados> obterListaFeriadosEntreDatas(Date dataInicio,
			Date dataFim) {
		return getAghFeriadosDAO().obterListaFeriadosEntreDatas(dataInicio,
				dataFim);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(
			Object objPesquisa, boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos, Object[] caracteristicas) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(
						objPesquisa, ordernarPorCodigoAlaDescricao,
						apenasAtivos, caracteristicas);
	}

	@Override
	public List<AghAtendimentoPacientes> listarAtendimentosPacientesPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAghAtendimentoPacientesDAO()
				.listarAtendimentosPacientesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void persistirAghAtendimentoPacientes(
			AghAtendimentoPacientes aghAtendimentoPacientes) {
		this.getAghAtendimentoPacientesDAO().persistir(aghAtendimentoPacientes);
	}

	protected AghDocumentosAssinadosDAO getAghDocumentosAssinadosDAO() {
		return aghDocumentosAssinadosDAO;
	}

	protected AghNodoPolDAO getAghNodoPolDAO() {
		return aghNodoPolDAO;
	}

	@Override
	public AghAtendimentos obterAtendimentoPorConsulta(Integer numero) {
		return getAghAtendimentoDAO().primeiroAtendimentosPorConsulta(numero);
	}

	@Override
	public AghProfEspecialidades findProfEspecialidadesById(
			Integer serMatricula, Short serVinCodigo, Short espSeq) {
		AghProfEspecialidadesId id = new AghProfEspecialidadesId(serMatricula, serVinCodigo, espSeq);
		return getAghProfEspecialidadesDAO().obterPorChavePrimaria(id, AghProfEspecialidades.Fields.RAP_SERVIDOR);
		
		
	}

	@Override
	@Secure("#{s:hasPermission('cidPorEquipeConsulta','pesquisar')}")
	public List<AghEquipes> getListaEquipesAtivas(String objPesquisa) {
		return getAghEquipesDAO().pesquisarPorNomeCodigoAtiva(objPesquisa);
	}
	

	@Override
	public List<AghEquipes> getPesquisaEquipesAtivas(String objPesquisa) {
		return getAghEquipesDAO().pesquisarEquipeAtiva(objPesquisa);
	}
	
	@Override
	public Long getListaEquipesAtivasCount(String objPesquisa) {
		return getAghEquipesDAO().pesquisarEquipeAtivaCount(objPesquisa);
	}	

	@Override
	public List<AghEquipes> getListaEquipes(String objPesquisa) {
		return getAghEquipesDAO().pesquisarPorNomeOuCodigo(objPesquisa);
	}

	@Override
	public List<AghCid> listarCidPorProcedimento(Object parametro,
			Integer phiSeq) {
		return getAghCidsDAO()
				.pesquisarCidsProcedimentoAmbulatorioPorDescricaoOuId(
						parametro, phiSeq);
	}

	@Override
	public Long listarCidPorProcedimentoCount(Object parametro,
			Integer phiSeq) {
		return getAghCidsDAO()
				.pesquisarCidsProcedimentoAmbulatorioPorDescricaoOuIdCount(
						parametro, phiSeq);
	}

	@Override
	public List<AghUnidadesFuncionais> listaUnidadeFuncionalComSiglaNaoNulla(
			Object param, DominioSituacao situacao, Integer maxResults,
			String order) {
		return getAghUnidadesFuncionaisDAO()
				.listaUnidadeFuncionalComSiglaNaoNulla(param, situacao,
						maxResults, order);
	}

	@Override
	public Long listaUnidadeFuncionalComSiglaNaoNullaCount(Object param,
			DominioSituacao situacao) {
		return getAghUnidadesFuncionaisDAO()
				.listaUnidadeFuncionalComSiglaNaoNullaCount(param, situacao);
	}

	@Override
	public List<Object[]> pesquisarEspCrmVOAmbulatorioEquip(Object parametro,
			AghEquipes equipe, AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return getAghProfEspecialidadesDAO()
				.pesquisarEspCrmVOAmbulatorioEquipe(parametro, equipe,
						especialidade);
	}

	@Override
	public List<Object[]> pesquisarEspCrmVOAmbulatorioEspecialidade(
			Object parametro, AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return getAghProfEspecialidadesDAO()
				.pesquisarEspCrmVOAmbulatorioEspecialidade(parametro,
						especialidade);
	}

	@Override
	public List<AghEquipes> pesquisarEquipesPorEspecialidadeServidores(
			Object objPesquisa, AghEspecialidades especialidade,
			DominioSituacao situacao) throws ApplicationBusinessException {
		return this.getAghEspecialidadesDAO()
				.pesquisarEquipesPorEspecialidadeServidores(objPesquisa,
						especialidade, situacao);
	}

	@Override
	public void inserirAghAtendimentoJn(AghAtendimentoJn aghAtendimentoJn) {
		this.getAghAtendimentoJnDAO().persistir(aghAtendimentoJn);
		this.getAghAtendimentoJnDAO().flush();
	}

	@Override
	public void inserirAghAtendimentoJn(AghAtendimentoJn aghAtendimentoJn,
			boolean flush) {
		this.getAghAtendimentoJnDAO().persistir(aghAtendimentoJn);
		if (flush) {
			this.getAghAtendimentoJnDAO().flush();
		}
	}

	protected AghAtendimentoJnDAO getAghAtendimentoJnDAO() {
		return aghAtendimentoJnDAO;
	}

	@Override
	public List<AghUnidadesFuncionais> obterUnidadesFuncionais(Object param) {
		return getAghUnidadesFuncionaisDAO().obterUnidadesFuncionais(param);
	}

	@Override
	public void atualizarAghUnidadesFuncionais(
			AghUnidadesFuncionais aghUnidadesFuncionais)
			throws ApplicationBusinessException {
		getAghUnidadesFuncionaisRN().atualizarAghUnidadesFuncionais(
				aghUnidadesFuncionais);
	}

	protected AghUnidadesFuncionaisRN getAghUnidadesFuncionaisRN() {
		return aghUnidadesFuncionaisRN;
	}

	@Override
	public AghAtendimentos obterUltimoAtendimentoPorCodigoEOrigem(
			Integer prontuario, DominioOrigemAtendimento... origemAtendimentos) {
		return getAghAtendimentoDAO().obterUltimoAtendimentoPorCodigoEOrigem(
				prontuario, origemAtendimentos);
	}
	
	@Override
	public List<AghAtendimentos> obterAtendimentoPorCodigoEOrigem(final Integer codPaciente) {
		return getAghAtendimentoDAO().obterAtendimentoPorCodigoEOrigem(codPaciente);
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadeFarmacia() {
		return getAghUnidadesFuncionaisDAO().listarUnidadeFarmacia();
	}

	@Override
	public AghImpressoraPadraoUnids obterImpressoraPadraoUnidPorAtendimento(
			AghAtendimentos atendimento) {
		return getAghImpressoraPadraoUnidsDAO()
				.obterImpressoraPadraoUnidPorAtendimento(atendimento);
	}

	@Override
	public String pesquisarDescricaoCidPrincipal(final Integer seqAtendimento,
			final Integer apaSeq, final Short seqp) {
		return getCidON().pesquisarDescricaoCidPrincipal(seqAtendimento,
				apaSeq, seqp);
	}

	@Override
	public List<String> pesquisarDescricaoCidSecundario(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp) {
		return getCidON().pesquisarDescricaoCidSecundario(seqAtendimento,
				apaSeq, seqp);
	}

	@Override
	public List<AghCid> obterCids(final String codDesc, final boolean filtroSituacaoAtiva) {
		return getCidON().obterCids(codDesc, filtroSituacaoAtiva);
	}
	
	@Override
	public Long obterCidsCount(final String codDesc, final boolean filtroSituacaoAtiva) {
		return getCidON().obterCidsCount(codDesc, filtroSituacaoAtiva);
	}	

	@Override
	public AghCid obterCid(String codigo) {
		return this.getAghCidsDAO().obterCid(codigo);
	}

	@Override
	public List<AghCid> pesquisarCidsPorGrupo(final Integer seqGrupoCid) {
		return getCidON().pesquisarCidsPorGrupo(seqGrupoCid);
	}

	@Override
	public Long pesquisarProcedimentosParaCidCount(final String cidCodigo) {
		return getCidON().pesquisarProcedimentosParaCidCount(cidCodigo);
	}

	@Override
	public List<CidVO> pesquisarProcedimentosParaCid(
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc, final String codCid) {
		return getCidON().pesquisarProcedimentosParaCid(firstResult,
				maxResults, orderProperty, asc, codCid);
	}

	@Override
	public List<AghCid> pesquisarCidsRelacionados(final Integer seqCid) {
		return getCidON().pesquisarCidsRelacionados(seqCid);
	}

	@Override
	@BypassInactiveModule
	public List<AghCid> pesquisarCidsPorDescricaoOuId(final String descricao,
			final Integer limiteRegistros) {
		return getCidON().pesquisarCidsPorDescricaoOuId(descricao,
				limiteRegistros);
	}

	@Override
	public List<AghCid> pesquisarCidsPorDescricaoOuId(String descricao,
			Integer limiteRegistros, Boolean semJoin) {
		return aghCidDAO.pesquisarCidsPorDescricaoOuId(descricao, limiteRegistros, semJoin);
	}
	
	@Override
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(
			final String descricao, final Integer limiteRegistros)
			throws ApplicationBusinessException {
		return getCidON().pesquisarCidsSemSubCategoriaPorDescricaoOuId(
				descricao, limiteRegistros);
	}

	@Override
	public AghAtendimentos obterAtendimentoComPaciente(Integer seqAtendimento) {
		return this.getAghAtendimentoDAO().obterAtendimentoComPaciente(seqAtendimento);
	}
	
	@Override
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(
			final String descricao, final Integer limiteRegistros)
			throws ApplicationBusinessException {
		return getCidON()
				.pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(
						descricao, limiteRegistros);
	}

	@Override
	@BypassInactiveModule
	public List<AinCidsInternacao> pesquisarCidsInternacao(
			final Integer seqInternacao) {
		return getCidON().pesquisarCidsInternacao(seqInternacao);
	}

	@Override
	@BypassInactiveModule
	public AghCid obterCid(final Integer seq) {
		return getCidON().obterCid(seq);
	}

	@Override
	public void validarCodigoCid(final String codCid)
			throws ApplicationBusinessException {
		getCidON().validarCodigoCid(codCid);
	}

	protected CidON getCidON() {
		return cidON;
	}

	/**
	 * DEPRECATED = Usar AghuFacade.verificarCaracteristicaUnidadeFuncional pois retorna Boolean e não DominioSimNao.
	 */
	@Override
	@Deprecated
	public DominioSimNao verificarCaracteristicaDaUnidadeFuncional(
			Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return this.getAghCaractUnidFuncionaisDAO()
				.verificarCaracteristicaDaUnidadeFuncional(unfSeq,
						caracteristica);
	}
	
	@Override
	public Boolean verificarCaracteristicaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return this.getAghCaractUnidFuncionaisDAO().verificarCaracteristicaUnidadeFuncional(unfSeq,	caracteristica);
	}

	@Override
	public String obterDescricaoVAghUnidFuncional(Short seq) {
		return getVAghUnidFuncionalDAO().obterDescricaoVAghUnidFuncional(seq);
	}

	public VAghUnidFuncionalDAO getVAghUnidFuncionalDAO() {
		return vAghUnidFuncionalDAO;
	}
	
	@Override
	public VAghUnidFuncional obterVAghUnidFuncionalPorChavePrimaria(VAghUnidFuncionalId unidFuncionalId) {
		return getVAghUnidFuncionalDAO().obterPorChavePrimaria(unidFuncionalId);
	}	

	@Override
	public AghCaractUnidFuncionais buscarCaracteristicaPorUnidadeCaracteristica(
			Short seqUnidadeFunc, ConstanteAghCaractUnidFuncionais areaFechada) {
		return getAghCaractUnidFuncionaisDAO()
				.buscarCaracteristicaPorUnidadeCaracteristica(seqUnidadeFunc,
						areaFechada);
	}

	// @Override
	// public List<AghAtendimentos>
	// listarAtendimentosComInternacaoEmAndamentoPorPaciente(Integer
	// codigoPaciente) {
	// return
	// this.getAghAtendimentoDAO().listarAtendimentosComInternacaoEmAndamentoPorPaciente(codigoPaciente);
	// }
	//
	// @Override
	// public List<AghAtendimentos>
	// listarAtendimentoPorAtendimentoPacienteExterno(AghAtendimentosPacExtern
	// atendimentoPacExterno) {
	// return
	// this.getAghAtendimentoDAO().listarPorAtendimentoPacienteExterno(atendimentoPacExterno);
	// }
	//
	// @Override
	// public AghAtendimentos obterAghAtendimentoOriginal(Integer seq) {
	// return this.getAghAtendimentoDAO().obterOriginal(seq);
	// }
	//

	@Override
	public List<AghAla> pesquisaAlaList(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, String codigo, String descricao) {
		return this.getAghAlaDAO().pesquisaAlaList(firstResult, maxResult,
				orderProperty, asc, codigo, descricao);
	}

	@Override
	public Long pesquisaAlaListCount(String codigo, String descricao) {
		return this.getAghAlaDAO().pesquisaAlaListCount(codigo, descricao);
	}

	@Override
	public AghAla obterAghAlaPorChavePrimaria(String codigo) {
		return this.getAghAlaDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public void excluirAghAla(AghAla aghAla) {
		this.getAghAlaDAO().remover(aghAla);
	}

	@Override
	public void atualizarAghAla(AghAla aghAla) {
		this.getAghAlaDAO().atualizar(aghAla);
	}

	@Override
	public void persistirAghAla(AghAla aghAla) {
		this.getAghAlaDAO().persistir(aghAla);
	}

	@Override
	public void desatacharAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio) {
		this.getAghProfissionaisEspConvenioDAO().desatachar(
				aghProfissionaisEspConvenio);
	}

	@Override
	public AghProfissionaisEspConvenio obterAghProfissionaisEspConvenioPorChavePrimaria(
			AghProfissionaisEspConvenioId id) {
		return this.getAghProfissionaisEspConvenioDAO().obterPorChavePrimaria(
				id);
	}
	
	@Override	
	public List<AghProfissionaisEspConvenio> obterAghProfissionaisEspConvenioPorProfissionalEsp(
			Integer preSerMatricula, Short preSerVinCodigo, Short preEspSeq) {
		return this.getAghProfissionaisEspConvenioDAO().obterAghProfissionaisEspConvenioPorProfissionalEsp(preSerMatricula,preSerVinCodigo,preEspSeq);
	}

	@Override
	public void removerAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio,
			boolean flush) {
		this.getAghProfissionaisEspConvenioDAO().remover(
				aghProfissionaisEspConvenio);
		if (flush){
			this.getAghProfissionaisEspConvenioDAO().flush();
		}
	}

	@Override
	public void persistirAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio) {
		this.getAghProfissionaisEspConvenioDAO().persistir(
				aghProfissionaisEspConvenio);
	}

	@Override
	public void atualizarAghProfissionaisEspConvenio(
			AghProfissionaisEspConvenio aghProfissionaisEspConvenio) {
		this.getAghProfissionaisEspConvenioDAO().atualizar(
				aghProfissionaisEspConvenio);
	}

	@Override
	public void limparEntityManager() {
		this.clear();
	}

	@Override
	public List<EspCrmVO> pesquisarEspCrmVO(final Object strPesquisa,
			final AghEspecialidades especialidade,
			final DominioSimNao ambulatorio, final Integer[] tiposQualificacao)
			throws ApplicationBusinessException {
		return this.getAghProfEspecialidadesDAO().pesquisarEspCrmVO(
				strPesquisa, especialidade, ambulatorio, tiposQualificacao);
	}

	@Override
	public EspCrmVO obterEspCrmVO(final Object strPesquisa,
			final AghEspecialidades especialidade,
			final DominioSimNao ambulatorio, final RapServidores servidor,
			final Integer[] tiposQualificacao) throws ApplicationBusinessException {
		return this.getAghProfEspecialidadesDAO().obterEspCrmVO(strPesquisa,
				especialidade, ambulatorio, servidor, tiposQualificacao);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public EspCrmVO obterCrmENomeMedicoPorServidor(
			final RapServidores servidor,
			final AghEspecialidades especialidade,
			final Integer qualificacaoMedicina) throws ApplicationBusinessException {
		return this.getAghProfEspecialidadesDAO()
				.obterCrmENomeMedicoPorServidor(servidor, especialidade,
						qualificacaoMedicina);
	}

	@Override
	public List<AghProfEspecialidades> listarProfEspecialidades(
			Integer matricula, Integer codigoVinculo,
			Integer codigoEspecialidade) {
		return this.getAghProfEspecialidadesDAO().listarProfEspecialidades(
				matricula, codigoVinculo, codigoEspecialidade);
	}

	@Override
	public Integer obterQtdContaInternados(Short serVinCodigo,
			Integer serMatricula, Short espSeq) {
		return this.getAghProfEspecialidadesDAO().obterQtdContaInternados(
				serVinCodigo, serMatricula, espSeq);
	}

	@Override
	public void persistirAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades) {
		this.getAghProfEspecialidadesDAO().persistir(aghProfEspecialidades);
	}
	
	@Override
	public void atualizarAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades) {
		this.getAghProfEspecialidadesDAO().atualizar(aghProfEspecialidades);
	}
	
	@Override
	public void mergeAghProfEspecialidades(
			AghProfEspecialidades aghProfEspecialidades) {
		this.getAghProfEspecialidadesDAO().merge(aghProfEspecialidades);
	}

	@Override
	public List<ProfConveniosListVO> pesquisaProfConvenioslist(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer vinCodigo, Integer matricula, String nome,
			Long cpf, String siglaEspecialidade) {
		return this.getAghProfEspecialidadesDAO().pesquisaProfConvenioslist(
				firstResult, maxResult, orderProperty, asc, vinCodigo,
				matricula, nome, cpf, siglaEspecialidade);
	}

	@Override
	public Integer pesquisaProfConveniosListCount(Integer vinCodigo,
			Integer matricula, String nome, Long cpf, String siglaEspecialidade) {
		return this.getAghProfEspecialidadesDAO()
				.pesquisaProfConveniosListCount(vinCodigo, matricula, nome,
						cpf, siglaEspecialidade);
	}

	@Override
	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(
			AghEspecialidades especialidade) throws ApplicationBusinessException {
		return this.getAghProfEspecialidadesDAO()
				.pesquisarReferencialEspecialidadeProfissonalGridVOCount(
						especialidade);
	}

	@Override
	public List<Object[]> pesquisarReferencialEspecialidadeProfissonalGridVO(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return this.getAghProfEspecialidadesDAO()
				.pesquisarReferencialEspecialidadeProfissonalGridVO(
						firstResult, maxResult, orderProperty, asc,
						especialidade);
	}

	protected AghGrupoCidsDAO getAghGrupoCidsDAO() {
		return aghGrupoCidsDAO;
	}

	@Override
	public List<AghGrupoCids> pesquisarGrupoCidSIGLA(String sigla) {
		return this.getAghGrupoCidsDAO().pesquisarGrupoCidSIGLA(sigla);
	}

	@Override
	public List<AghGrupoCids> pesquisarGrupoCidSIGLAS(String sigla) {
		return this.getAghGrupoCidsDAO().pesquisarGrupoCidSIGLAS(sigla);
	}

	@Override
	public AghGrupoCids obterGrupoCidPorSigla(String sigla) {
		return this.getAghGrupoCidsDAO().obterGrupoCidPorSigla(sigla);
	}

	@Override
	public AghGrupoCids obterGrupoCidPorId(Integer cpcSeq, Integer seq) {
		return this.getAghGrupoCidsDAO().obterGrupoCidPorId(cpcSeq, seq);
	}

	@Override
	public Long pesquisarGruposCidsCount(AghCapitulosCid capitulo,
			Integer codigoGrupo, String siglaGrupo, String descricaoGrupo,
			DominioSituacao situacaoGrupo) {
		return this.getAghGrupoCidsDAO().pesquisarGruposCidsCount(capitulo,
				codigoGrupo, siglaGrupo, descricaoGrupo, situacaoGrupo);
	}

	@Override
	public boolean containsAghGrupoCids(AghGrupoCids aghGrupoCids) {
		return this.getAghGrupoCidsDAO().contains(aghGrupoCids);
	}

	@Override
	public AghGrupoCids mergeAghGrupoCids(AghGrupoCids aghGrupoCids) {
		return this.getAghGrupoCidsDAO().merge(aghGrupoCids);
	}

	@Override
	public void removerAghGrupoCids(AghGrupoCids aghGrupoCids) {
		aghGrupoCidsDAO.remover(aghGrupoCids);
		aghGrupoCidsDAO.flush();
	}

	@Override
	public void atualizarAghGrupoCids(AghGrupoCids aghGrupoCids) {
		aghGrupoCidsDAO.atualizar(aghGrupoCids);
		aghGrupoCidsDAO.flush();
	}

	@Override
	public List<AghGrupoCids> pesquisarGruposCids(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo,
			String descricaoGrupo, DominioSituacao situacaoGrupo) {
		return this.getAghGrupoCidsDAO().pesquisarGruposCids(firstResult,
				maxResults, orderProperty, asc, capitulo, codigoGrupo,
				siglaGrupo, descricaoGrupo, situacaoGrupo);
	}

	@Override
	public void inserirAghGrupoCids(AghGrupoCids aghGrupoCids, boolean flush) {
		this.getAghGrupoCidsDAO().persistir(aghGrupoCids);
		if (flush) {
			this.getAghGrupoCidsDAO().flush();
		}
	}

	@Override
	public Integer obterSeqAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghGrupoCidsDAO().obterSeqAnterior(seq, cpcSeq);
	}

	@Override
	public String obterDescricaoAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghGrupoCidsDAO().obterDescricaoAnterior(seq, cpcSeq);
	}

	@Override
	public Date obterDataCriacaoAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghGrupoCidsDAO().obterDataCriacaoAnterior(seq, cpcSeq);
	}

	@Override
	public Integer obterMatriculaServidorAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghGrupoCidsDAO().obterMatriculaServidorAnterior(seq,
				cpcSeq);
	}

	@Override
	public Short obterVinCodigoServidorAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghGrupoCidsDAO().obterVinCodigoServidorAnterior(seq,
				cpcSeq);
	}

	@Override
	public List<AghGrupoCids> pesquisarGrupoCidPorCapituloCid(
			Integer seqCapituloCid) {
		return this.getAghGrupoCidsDAO().pesquisarGrupoCidPorCapituloCid(
				seqCapituloCid);
	}

	@Override
	public Integer obterCpcSeqAnterior(Integer seq, Integer cpcSeq) {
		return this.getAghGrupoCidsDAO().obterCpcSeqAnterior(seq, cpcSeq);
	}

	@Override
	public List<AghCaractUnidFuncionais> pesquisarCaracteristicaUnidadeFuncional(
			Short seq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return this.getAghCaractUnidFuncionaisDAO()
				.pesquisarCaracteristicaUnidadeFuncional(seq, caracteristica);
	}

	@Override
	public AghCaractUnidFuncionais obterAghCaractUnidFuncionaisPorChavePrimaria(
			AghCaractUnidFuncionaisId id) {
		return this.getAghCaractUnidFuncionaisDAO().obterPorChavePrimaria(id);
	}

	@Override
	public List<AghCid> listarCids(Integer gcdCpcSeq, Integer gcdSeq) {
		return this.getAghCidsDAO().listarCids(gcdCpcSeq, gcdSeq);
	}
	
	@Override
	public List<AghCid> listarAghCidPorIdadeSexo(List<Long> listaCodSsm, Integer idade, String cidCodigo,
			DominioSexoDeterminante sexo) {
		return this.getAghCidsDAO().listarAghCidPorIdadeSexo(listaCodSsm, idade, cidCodigo, sexo);
	}
	
	@Override
	public List<AghCid> listarCidPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao) {
		return this.getAghCidsDAO().listarCidPorIdadeSexoProcedimento(strPesquisa, sexo, idade, codTabela, paramTabelaFaturPadrao);
	}

	@Override
	public Long listarCidPorIdadeSexoProcedimentoCount(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade, Long codTabela,
			Short paramTabelaFaturPadrao) {
		return this.getAghCidsDAO().listarCidPorIdadeSexoProcedimentoCount(strPesquisa, sexo, idade, codTabela, paramTabelaFaturPadrao);
	}

	/**
	 * 
	 * @param objPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> pesquisarClinicasPorCodigoEDescricao(
			Object objPesquisa) {
		return getAghClinicasDAO().pesquisarClinicasPorCodigoEDescricao(
				objPesquisa);
	}

	/**
	 * Metodo para pesquisa paginada.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param codigoSUS
	 * @return
	 */
	@Override
	public Long pesquisaClinicaCount(final Integer codigoPesquisa,
			final String descricaoPesquisa, final Integer codigoSUSPesquisa) {
		return getAghClinicasDAO().pesquisaCount(codigoPesquisa,
				descricaoPesquisa, codigoSUSPesquisa);
	}

	/**
	 * Metodo para pesquisa paginada.
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param codigoSUS
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> pesquisaClinica(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer codigoPesquisa,
			final String descricaoPesquisa, final Integer codigoSUSPesquisa) {
		return getAghClinicasDAO().pesquisa(firstResult, maxResults,
				orderProperty, asc, codigoPesquisa, descricaoPesquisa,
				codigoSUSPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> listarClinicasPorNomeOuCodigo(
			final Object strPesquisa) {
		return getAghClinicasDAO().listarPorNomeOuCodigo(strPesquisa);
	}

	@Override
	public List<AghClinicas> pesquisarClinicasPorCodigoOuDescricao(
			final Object objPesquisa, final boolean ordenarPorCodigo,
			final boolean ordenarPorDesc) {
		return getAghClinicasDAO().pesquisarClinicasPorCodigoOuDescricao(
				objPesquisa, ordenarPorCodigo, ordenarPorDesc);
	}

	@Override
	public Long pesquisarClinicasPorCodigoOuDescricaoCount(
			final Object objPesquisa) {
		return getAghClinicasDAO().pesquisarClinicasPorCodigoOuDescricaoCount(
				objPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> obterClinicaCapacidadeReferencial(
			final String paramPesquisa) {
		return getAghClinicasDAO().obterClinicaCapacidadeReferencial(
				paramPesquisa);
	}

	/**
	 * 
	 * @dbtables AghClinicas select
	 * 
	 * @param strPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> pesquisarClinicasSolInternacao(
			final String strPesquisa) {
		return getAghClinicasDAO().pesquisarClinicasSolInternacao(strPesquisa);
	}

	/**
	 * @dbtables AghClinicas select
	 * 
	 * @param codigo
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public AghClinicas obterClinica(final Integer codigo) {
		return getAghClinicasDAO().obterClinica(codigo);
	}

	@Override
	public AghClinicas obterClinicaPorChavePrimaria(final Integer codigo) {
		return getAghClinicasDAO().obterPorChavePrimaria(codigo);
	}
	
	/**
	 * Pesquisa <b>AghClinicas</b> com codigo igual a <i>strPesquisa</i><br>
	 * ou descricao contendo <i>strPesquisa</i>.
	 * 
	 * @dbtables AghClinicas select
	 * @param strPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public List<AghClinicas> pesquisarClinicas(final String strPesquisa) {

		// Tenta buscar pelo ID único da clínica e caso encontre 1 elemento
		// válido, então retorna apenas ele.
		if (StringUtils.isNotBlank(strPesquisa)
				&& StringUtils.isNumeric(strPesquisa)) {
			AghClinicas clinica = getAghClinicasDAO().obterClinica(
					Integer.valueOf(strPesquisa));
			if (clinica != null) {
				List<AghClinicas> lista = new ArrayList<AghClinicas>(1);
				lista.add(clinica);
				return lista;
			}
		}

		// Caso não ache 1 elemento pelo ID, tenta buscar por outras informações
		return getAghClinicasDAO().pesquisarClinicas(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('clinica','pesquisar')}")
	public Integer pesquisarClinicasHQLCount(final String strPesquisa) {
		return getAghClinicasDAO().pesquisarClinicasHQLCount(strPesquisa);
	}

	@Override
	public Long countClinicaReferencial(AghClinicas clinica)
			throws ApplicationBusinessException {
		return this.getAghClinicasDAO().countClinicaReferencial(clinica);
	}

	@Override
	public List<PesquisaReferencialClinicaEspecialidadeVO> listaClinicaferencial(
			AghClinicas clinica) throws ApplicationBusinessException {
		return this.getAghClinicasDAO().listaClinicaferencial(clinica);
	}

	@Override
	public List<AghClinicas> pesquisaSituacaoLeitos(AghClinicas clinica,
			String orderProperty, Boolean asc) {
		return this.getAghClinicasDAO().pesquisaSituacaoLeitos(clinica,
				orderProperty, asc);
	}

	@Override
	public List<AghClinicas> pesquisaSituacaoLeitos(AghClinicas clinica) {
		return this.getAghClinicasDAO().pesquisaSituacaoLeitos(clinica);
	}

	@Override
	public AghClinicas obterClinicaPelaDescricaoExata(String descricao) {
		return this.getAghClinicasDAO().obterClinicaPelaDescricaoExata(
				descricao);
	}

	@Override
	public void inserirAghClinicas(AghClinicas aghClinicas) {
		this.getAghClinicasDAO().persistir(aghClinicas);
		this.getAghClinicasDAO().flush();
	}

	@Override
	public void atualizarAghClinicasDepreciado(AghClinicas aghClinicas) {
		this.getAghClinicasDAO().merge(aghClinicas);
		this.getAghClinicasDAO().flush();
	}

	@Override
	public void removerAghClinicas(AghClinicas aghClinicas) {
		this.getAghClinicasDAO().remover(aghClinicas);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesPorClinica(
			AghClinicas clinica) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadesPorClinica(clinica);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(
			Object paramPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadesInternasPorSiglaENome(paramPesquisa);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(
			Object strPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadePorSiglaNome(strPesquisa);
	}

	@Override
	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadePorSiglaNomeCount(strPesquisa);
	}

	@Override
	public Long countEspecialidadeReferencial(AghClinicas clinica)
			throws ApplicationBusinessException {
		return this.getAghEspecialidadesDAO().countEspecialidadeReferencial(
				clinica);
	}

	@Override
	public List<PesquisaReferencialClinicaEspecialidadeVO> listaEspecialidadeReferencial(
			AghClinicas clinica) throws ApplicationBusinessException {
		return this.getAghEspecialidadesDAO().listaEspecialidadeReferencial(
				clinica);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSigla(
			Object paramPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadesInternasPorSigla(paramPesquisa);
	}

	@Override
	public Long pesquisarEspecialidadesInternasPorSiglaENomeCount(
			Object paramPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadesInternasPorSiglaENomeCount(
						paramPesquisa);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidades(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short codigoEspecialidade, String nomeEspecialidade,
			String siglaEspecialidade, Short codigoEspGenerica,
			Integer centroCusto, Integer clinica, DominioSituacao situacao) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidades(
				firstResult, maxResult, orderProperty, asc,
				codigoEspecialidade, nomeEspecialidade, siglaEspecialidade,
				codigoEspGenerica, centroCusto, clinica, situacao);
	}

	@Override
	public Long pesquisarEspecialidadesCount(Short codigo,
			String nomeEspecialidade, String siglaEspecialidade,
			Short codigoEspGenerica, Integer centroCusto, Integer clinica,
			DominioSituacao situacao) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadesCount(
				codigo, nomeEspecialidade, siglaEspecialidade,
				codigoEspGenerica, centroCusto, clinica, situacao);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeGenerica(
			String strPesquisa, Integer maxResults) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadeGenerica(
				strPesquisa, maxResults);
	}

	@Override
	public List<AghEspecialidades> pesquisarTodasEspecialidades(String strPesquisa){
		return this.getAghEspecialidadesDAO().pesquisarTodasEspecialidades(strPesquisa);
	}
	
	@Override
	public Long pesquisarTodasEspecialidadesCount(String strPesquisa){
		return this.getAghEspecialidadesDAO().pesquisarTodasEspecialidadesCount(strPesquisa);

	}

	@Override
	public Long pesquisarEspecialidadeGenericaCount(String strPesquisa) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadeGenericaCount(strPesquisa);
	}
		

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeInternacao(
			String strPesquisa, Short idadePaciente,
			DominioSimNao indUnidadeEmergencia, Integer maxResults) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadeInternacao(
				strPesquisa, idadePaciente, indUnidadeEmergencia, maxResults);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeSolicitacaoInternacao(
			String strPesquisa, Short idadePaciente, Integer maxResults) {
		return this.getAghEspecialidadesDAO()
				.pesquisarEspecialidadeSolicitacaoInternacao(strPesquisa,
						idadePaciente, maxResults);
	}
	
	
	@Override
	public AghEspecialidades obterEspecialidade(Short seq) {
		return this.getAghEspecialidadesDAO().obterEspecialidade(seq, LockOptions.UPGRADE);
	}
	

	@Override
	public void desatacharAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.getAghEspecialidadesDAO().desatachar(aghEspecialidades);
	}

	@Override
	public void atualizarAghEspecialidades(AghEspecialidades aghEspecialidades,
			boolean flush) {
		this.getAghEspecialidadesDAO().merge(aghEspecialidades);
		if (flush) {
			this.getAghEspecialidadesDAO().flush();
		}
	}

	@Override
	public void persistirAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.getAghEspecialidadesDAO().persistir(aghEspecialidades);
	}
	
	@Override
	public Long pesquisarEspecialidadePorSeqOuSiglaOuNomeCount(Object objPesquisa) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadePorSeqOuSiglaOuNomeCount(objPesquisa);
	}
	

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadePorSeqOuSiglaOuNome(Object strPesquisa) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadePorSeqOuSiglaOuNome(strPesquisa);
	}

	@Override
	public AghEspecialidades obterEspecialidadePorSigla(String sigla) {
		return this.getAghEspecialidadesDAO().obterEspecialidadePorSigla(sigla);
	}

	@Override
	public List<AghEspecialidades> obterEspecialidadePediatria() {
		return this.getAghEspecialidadesDAO().obterEspecialidadePediatria();
	}

	@Override
	public SituacaoLeitosVO pesquisaSituacaoSemLeitos() {
		return this.getAghAtendimentoDAO().pesquisaSituacaoSemLeitos();
	}
	
	@Override
	public List<AinInternacao> listarInternacoesDoAtendimento(
			Integer seqAtendimento, Date novaData) {
		return this.getAghAtendimentoDAO().listarInternacoesDoAtendimento(
				seqAtendimento, novaData);
	}

	@Override
	public List<Object[]> obterCensoUnion19(Short unfSeq, Short unfSeqMae,
			Date data, DominioSituacaoUnidadeFuncional status) {
		return this.getAghAtendimentoDAO().obterCensoUnion19(unfSeq, unfSeqMae,
				data, status);
	}

	@Override
	public List<VAinAltasVO> pesquisaPacientesComAlta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Date dataInicial, Date dataFinal, boolean altaAdministrativa,
			DominioTipoAlta tipoAlta,
			DominioOrdenacaoPesquisaPacComAlta ordenacao, Short unidFuncSeq,
			Short espSeq, String tamCodigo) {
		return this.getAghAtendimentoDAO().pesquisaPacientesComAlta(
				firstResult, maxResult, orderProperty, asc, dataInicial,
				dataFinal, altaAdministrativa, tipoAlta, ordenacao,
				unidFuncSeq, espSeq, tamCodigo);
	}

	@Override
	public Long pesquisaPacientesComAltaCount(Date dataInicial,
			Date dataFinal, boolean altaAdministrativa,
			DominioTipoAlta tipoAlta, Short unidFuncSeq, Short espSeq,
			String tamCodigo) {
		return this.getAghAtendimentoDAO().pesquisaPacientesComAltaCount(
				dataInicial, dataFinal, altaAdministrativa, tipoAlta,
				unidFuncSeq, espSeq, tamCodigo);
	}

	@Override
	public List<Object[]> pesquisarProcedenciaPaciente(Short convSusPadrao) {
		return this.getAghAtendimentoDAO().pesquisarProcedenciaPaciente(
				convSusPadrao);
	}

	@Override
	public AghAtendimentos obtemAtendimentoPaciente(AipPacientes paciente) {
		return this.getAghAtendimentoDAO().obtemAtendimentoPaciente(paciente);
	}

	@Override
	public AghAtendimentos buscaAtendimentoPorNumeroConsulta(
			Integer numeroConsulta) {
		return this.getAghAtendimentoDAO().buscaAtendimentoPorNumeroConsulta(
				numeroConsulta);
	}

	@Override
	public boolean possuiRecemNascido(AghAtendimentos atendimento) {
		return this.getAghAtendimentoDAO().possuiRecemNascido(atendimento);
	}

	protected AghCapitulosCidDAO getAghCapitulosCidDAO() {
		return aghCapitulosCidDAO;
	}

	@Override
	public AghCapitulosCid obterAghCapitulosCidPorChavePrimaria(Integer seq) {
		return this.getAghCapitulosCidDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public AghCapitulosCid obterAghCapitulosCidPorNumero(Short numero, Integer seq) {
		return this.getAghCapitulosCidDAO().obterCapituloCidPorNumero(numero, seq);
	}

	@Override
	public DominioSituacao obterSituacaoCapituloCid(Integer seqCapitulo) {
		return this.getAghCapitulosCidDAO().obterSituacaoCapituloCid(
				seqCapitulo);
	}

	@Override
	public List<AghCapitulosCid> pesquisarCapitulosCidsAtivo() {
		return this.getAghCapitulosCidDAO().pesquisarCapitulosCidsAtivo();
	}

	@Override
	public Long obterCapituloCidCount(Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao) {
		return this.getAghCapitulosCidDAO().obterCapituloCidCount(numero,
				descricao, indExigeCidSecundario, indSituacao);
	}

	@Override
	public String obterDescricaoAnterior(Integer seq) {
		return this.getAghCapitulosCidDAO().obterDescricaoAnterior(seq);
	}

	@Override
	public Integer obterMatriculaServidorAnterior(Integer seq) {
		return this.getAghCapitulosCidDAO().obterMatriculaServidorAnterior(seq);
	}

	@Override
	public Short obterVinCodigoServidorAnterior(Integer seq) {
		return this.getAghCapitulosCidDAO().obterVinCodigoServidorAnterior(seq);
	}

	@Override
	public List<AghCapitulosCid> pesquisarAghCapitulosCid(Integer firstResult,
			Integer maxResults, Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao) {
		return this.getAghCapitulosCidDAO().pesquisar(firstResult, maxResults,
				numero, descricao, indExigeCidSecundario, indSituacao);
	}

	@Override
	public Date obterDataCriacaoAnterior(Integer seq) {
		return this.getAghCapitulosCidDAO().obterDataCriacaoAnterior(seq);
	}

	@Override
	public Integer obterSeqAnterior(Integer seq) {
		return this.getAghCapitulosCidDAO().obterSeqAnterior(seq);
	}

	@Override
	public void inserirAghCapitulosCid(AghCapitulosCid aghCapitulosCid) {
		this.getAghCapitulosCidDAO().persistir(aghCapitulosCid);
	}
	
	@Override
	public void atualizarAghCapitulosCid(AghCapitulosCid aghCapitulosCid) {
		this.getAghCapitulosCidDAO().atualizar(aghCapitulosCid);
	}

	@Override
	public void removerAghCapitulosCid(AghCapitulosCid aghCapitulosCid) {
		this.getAghCapitulosCidDAO().remover(aghCapitulosCid);
	}

	@Override
	public List<AghAtendimentos> pesquisaFoneticaAtendimentos(
			String nomePesquisaFonetica, String leitoPesquisaFonetica,
			String quartoPesquisaFonetica,
			AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada) {
		return getAghAtendimentoDAO().pesquisaFoneticaAtendimentos(
				nomePesquisaFonetica, leitoPesquisaFonetica,
				quartoPesquisaFonetica,
				unidadeFuncionalPesquisaFoneticaSelecionada);
	}

	@Override
	public List<AghAtendimentos> listarPaciente(RapServidores servidor,
			List<RapServidores> pacAtdEqpResp, boolean mostrarPacientesCpa) {
		return getAghAtendimentoDAO().listarPaciente(servidor, pacAtdEqpResp,
				mostrarPacientesCpa);
	}

	@Override
	public AghAtendimentos obterAtendimentoRecemNascido(Integer gsoPacCodigo,
			Byte seqp) {
		return getAghAtendimentoDAO().obterAtendimentoRecemNascido(
				gsoPacCodigo, seqp);
	}

	@Override
	public AghAtendimentos obterAtendimentoRecemNascido(Integer gsoPacCodigo) {
		return getAghAtendimentoDAO()
				.obterAtendimentoRecemNascido(gsoPacCodigo);
	}

	@Override
	public AghAtendimentos obterAtendimentoOriginal(Integer novoAtdSeq) {
		return getAghAtendimentoDAO().obterAtendimentoOriginal(novoAtdSeq);
	}

	@Override
	public Date obterDataFimAtendimento(Integer atdSeq) {
		return getAghAtendimentoDAO().obterDataFimAtendimento(atdSeq);
	}

	@Override
	public Date obterDataInicioAtendimento(final Integer atdSeq) {
		return getAghAtendimentoDAO().obterDataInicioAtendimento(atdSeq);
	}

	@Override
	public List<AghAtendimentos> pesquisarPorProntuarioPacienteLista(
			Integer prontuario) {
		return getAghAtendimentoDAO().pesquisarPorProntuarioPacienteLista(
				prontuario);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorAtendimentoUrgenciaVigente(
			Integer cAtuSeq) {
		return getAghAtendimentoDAO()
				.obterAtendimentoPorAtendimentoUrgenciaVigente(cAtuSeq);
	}

	@Override
	public void refreshAndLock(AghAtendimentos atendimento) {
		getAghAtendimentoDAO().refreshAndLock(atendimento);
	}

	@Override
	public AghAtendimentos obterPorAtendimentoVigente(final Integer cAtdSeq) {
		return getAghAtendimentoDAO().obterPorAtendimentoVigente(cAtdSeq);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorHospDiaVigente(
			final Integer cHodSeq) {
		return getAghAtendimentoDAO()
				.obterAtendimentoPorHospDiaVigente(cHodSeq);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorInternacaoVigente(
			final Integer cIntSeq) {
		return getAghAtendimentoDAO().obterAtendimentoPorInternacaoVigente(
				cIntSeq);
	}
	
	@Override
	public List<AghAtendimentos> buscaAtendimentosSumarioPrescricao(
			final Date dataInicio, final Date dataFim)
			throws ApplicationBusinessException {
		return this.getAghAtendimentoDAO().buscaAtendimentosSumarioPrescricao(
				dataInicio, dataFim);
	}

	/**
	 * Obtem atendimento a partir de int_seq
	 * 
	 * @author andremachado
	 * @param c_int_seq
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public AghAtendimentos obterAtendimentoPorIntSeq(final Integer cIntSeq) {
		return this.getAghAtendimentoDAO().obterAtendimentoPorIntSeq(cIntSeq);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoVigentePrescricaoEnfermagemPorLeito(
			final AinLeitos leito) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoVigentePrescricaoEnfermagemPorLeito(leito);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoVigenteExameLimitadoPorLeito(
			final AinLeitos leito) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoVigenteExameLimitadoPorLeito(leito);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoVigenteExameLimitadoPorProntuario(
			final Integer prontuario) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoVigenteExameLimitadoPorProntuario(
						prontuario);
	}
	
	@Override
	public Boolean verificarAtendimentoVigentePorCodigo(final Integer codigo) {
		return getAghAtendimentoDAO().verificarAtendimentoVigentePorCodigo(codigo);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoVigentePrescricaoEnfermagem(
			final AipPacientes paciente) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoVigentePrescricaoEnfermagem(paciente);
	}

	@Override
	public void persistirAghCaractUnidFuncionaisJn(
			AghCaractUnidFuncionaisJn aghCaractUnidFuncionaisJn) {
		this.getAghCaractUnidFuncionaisJnDAO().persistir(
				aghCaractUnidFuncionaisJn);
	}

	@Override
	public void persistirAghUnidadesFuncionaisJn(
			AghUnidadesFuncionaisJn aghUnidadesFuncionaisJn) {
		this.getAghUnidadesFuncionaisJnDAO().persistir(aghUnidadesFuncionaisJn);
	}

	/**
	 * Recupera a entidade AghEquipe por chave primária.
	 * 
	 * @param seqEquipe
	 * @return
	 */
	@Override
	public AghEquipes obterEquipe(final Integer seqEquipe) {
		return this.getAghEquipesDAO().obterPorChavePrimaria(seqEquipe);
	}
	
	/**
	 * Método responsável pela persistência (inclusão e edição) do pojo
	 * AghEquipe.
	 * 
	 * @param aghEquipe
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('equipe','alterar')}")
	public void persistirEquipe(final AghEquipes aghEquipe,List<RapServidoresVO> profissionaisEquipe)
			throws ApplicationBusinessException {
		this.getEquipeCRUD().persistirEquipe(aghEquipe, profissionaisEquipe);
	}
	
	@Secure("#{s:hasPermission('equipe','excluir')}")
	public void removerEquipe(final AghEquipes aghEquipe) {
		this.getEquipeCRUD().removerEquipe(aghEquipe);
	}	


	/**
	 * Conta a quantidade de equipes de acordo com os critérios de pesquisa
	 * passados por parâmetro.
	 * 
	 * @param codigoPesquisaEquipe
	 * @param nomePesquisaEquipe
	 * @param responsavelPesquisaEquipe
	 * @param ativoPesquisaEquipe
	 * @param placarRiscoNeonatalPesquisaEquipe
	 * @return
	 */
	@Override
	public Long pesquisaEquipesCount(final Integer codigoPesquisaEquipe,
			final String nomePesquisaEquipe,
			final RapServidoresVO responsavelPesquisaEquipe,
			final DominioSituacao ativoPesquisaEquipe,
			final DominioSimNao placarRiscoNeonatalPesquisaEquipe) {

		return getAghEquipesDAO().pesquisaEquipesCount(codigoPesquisaEquipe,
				nomePesquisaEquipe, responsavelPesquisaEquipe,
				ativoPesquisaEquipe, placarRiscoNeonatalPesquisaEquipe);
	}

	/**
	 * Pesquisa Equipes de acordo com os critérios de pesquisa passados por
	 * parâmetro.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigoPesquisaEquipe
	 * @param nomePesquisaEquipe
	 * @param responsavelPesquisaEquipe
	 * @param ativoPesquisaEquipe
	 * @param placarRiscoNeonatalPesquisaEquipe
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('equipe','pesquisar')}")
	public List<AghEquipes> pesquisarEquipes(final Integer firstResult,
			final Integer maxResults, final String orderProperty,
			final boolean asc, final Integer codigoPesquisaEquipe,
			final String nomePesquisaEquipe,
			final RapServidoresVO responsavelPesquisaEquipe,
			final DominioSituacao ativoPesquisaEquipe,
			final DominioSimNao placarRiscoNeonatalPesquisaEquipe) {

		return getAghEquipesDAO().pesquisarEquipes(firstResult, maxResults,
				orderProperty, asc, codigoPesquisaEquipe, nomePesquisaEquipe,
				responsavelPesquisaEquipe, ativoPesquisaEquipe,
				placarRiscoNeonatalPesquisaEquipe);
	}

	/**
	 * Pesquisa equipes Ativas por nome ou por código.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('equipe','pesquisar')}")
	public List<AghEquipes> pesquisarEquipesAtivasPorNomeOuCodigo(
			final Object paramPesquisa, int quantidadeParaRetorno) {
		return getAghEquipesDAO().pesquisarAtivasPorNomeOuCodigo(paramPesquisa, quantidadeParaRetorno);
	}

	/**
	 * Pesquisa equipes porm nome ou por código.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('equipe','pesquisar')}")
	public List<AghEquipes> pesquisarEquipesPorNomeOuCodigo(
			final Object paramPesquisa, int quantidadeParaRetorno) {
		return getAghEquipesDAO().pesquisarPorNomeOuCodigo(paramPesquisa, quantidadeParaRetorno);
	}

	/**
	 * Pesquisa equpes por nome ou descrição
	 * 
	 * @param seqDesc
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('equipe','pesquisar')}")
	public List<AghEquipes> pesquisarEquipesPorNomeOuDescricao(
			final String seqDesc) {
		return this.getAghEquipesDAO().pesquisarEquipesPorNomeOuDescricao(
				seqDesc);

	}

	@Override
	public void inserirAghEspecialidadesJn(
			AghEspecialidadesJn aghEspecialidadesJn, boolean flush) {
		this.getAghEspecialidadesJnDAO().persistir(aghEspecialidadesJn);
		if (flush) {
			this.getAghEspecialidadesJnDAO().flush();
		}
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public String obterDescricaoUnidadeFuncional(final Short codigo) {
		return getAghUnidadesFuncionaisDAO().obterDescricaoUnidadeFuncional(
				codigo);
	}

	@Override
	public AghUnidadesFuncionais obterUnidadeFuncional(Short seq) {
		return this.getAghUnidadesFuncionaisDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public Object[] obterAndarAlaPorSeq(Short seq) {
		return this.getAghUnidadesFuncionaisDAO().obterAndarAlaPorSeq(seq);
	}

	@Override
	public void excluirAghUnidadesFuncionais(
			AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.getAghUnidadesFuncionaisDAO().remover(aghUnidadesFuncionais);
	}

	@Override
	public void persistirAghUnidadesFuncionais(
			AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.getAghUnidadesFuncionaisDAO().persistir(aghUnidadesFuncionais);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(
			Object parametro,
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional, Integer maxResult) {
		return this
				.getAghUnidadesFuncionaisRN()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(
						parametro, aghTiposUnidadeFuncional, maxResult);
	}

	@Override
	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(
			Object objPesquisa) {
		return this
				.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasCount(
						objPesquisa);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String strPesquisa) {
		return this.getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(strPesquisa,
				AghUnidadesFuncionaisDAO.ORDERNAR_POR_ANDAR, false, null);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisaUnidadesFuncionais(AghUnidadesFuncionais unidadeFuncional) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaUnidadesFuncionais(unidadeFuncional);
	}

	@Override
	public List<AghAtendimentos> pesquisaAtendimentos(AghUnidadesFuncionais unidadeFuncional, boolean ordemPorNome) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaAtendimentos(unidadeFuncional, ordemPorNome);
	}

	@Override
	public List<br.gov.mec.aghu.prescricaomedica.vo.AghAtendimentosVO> pesquisaAtendimentos(AghUnidadesFuncionais unidadeFuncional, DominioOrdenacaoRelatorioPacientesUnidade ordenacao) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaAtendimentos(unidadeFuncional, ordenacao);
	}

	@Override
	public Long pesquisaAtendimentosCount(final AghUnidadesFuncionais unidadeFuncional) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaAtendimentosCount(unidadeFuncional);
	}
	
	@Override
	public AghUnidadesFuncionais obterUnidadeFuncionalQuarto(Short numero) {
		return this.getAghUnidadesFuncionaisDAO().obterUnidadeFuncionalQuarto(numero);
	}

	@Override
	public AghUnidadesFuncionais obterAghUnidadesFuncionaisOriginal(
			AghUnidadesFuncionais aghUnidadesFuncionais) {
		return this.getAghUnidadesFuncionaisDAO().obterOriginal(
				aghUnidadesFuncionais);
	}

	@Override
	public Long pesquisaAghUnidadesFuncionaisCount(Short codigo,
			String descricao, String sigla, AghClinicas clinica,
			FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai,
			DominioSituacao situacao, String andar, AghAla ala) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaCount(codigo,
				descricao, sigla, clinica, centroCusto, unidadeFuncionalPai,
				situacao, andar, ala);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(
			String srtPesquisa) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalVOPorCodigoEDescricao(srtPesquisa);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalVOPorCodigoEDescricao(final String srtPesquisa, Integer maxResults) {
		return this.getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalVOPorCodigoEDescricao(srtPesquisa, maxResults);
	}
	
	@Override
	public Long pesquisarUnidadeFuncionalVOPorCodigoEDescricaoCount(final String srtPesquisa) {
		return this.getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalVOPorCodigoEDescricaoCount(srtPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorUnidEmergencia(
			String strPesquisa, boolean ordemPorDescricao) {
		List<AghUnidadesFuncionais> lista = this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorUnidEmergencia(strPesquisa,
						ordemPorDescricao);
		
		// Inicializar Ala
		for (AghUnidadesFuncionais aghUnidadeFuncional : lista) {
			Hibernate.initialize(aghUnidadeFuncional.getIndAla());
		}
		
		return lista;
	}

	@Override
	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacaoCount(
			Object objPesquisa) {
		return this
				.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacaoCount(
						objPesquisa);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(
			Object objPesquisa) {
		return this
				.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativasUnidadeInternacao(
						objPesquisa);
	}

	@Override
	public AghUnidadesFuncionais obterUnidadeFuncionalPorUnidEmergencia(
			Short codigo) {
		return this.getAghUnidadesFuncionaisDAO()
				.obterUnidadeFuncionalPorUnidEmergencia(codigo);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
			Object objPesquisa) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
						objPesquisa);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
			Object objPesquisa, boolean apenasAtivos) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoAtivaseInativas(
						objPesquisa, apenasAtivos);
	}

	@Override
	public AghUnidadesFuncionais atualizarAghUnidadesFuncionaisSemException(AghUnidadesFuncionais aghUnidadesFuncionais) {
		return this.getAghUnidadesFuncionaisDAO().merge(aghUnidadesFuncionais);
	}

	@Override
	public AghUnidadesFuncionais obterUnidadeFuncionalPorSigla(String sigla) {
		return this.getAghUnidadesFuncionaisDAO()
				.obterUnidadeFuncionalPorSigla(sigla);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			Short seq, Boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos,
			boolean caracteristicasInternacaoOuEmergencia,
			boolean caracteristicaUnidadeExecutora) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricao(seq,
						ordernarPorCodigoAlaDescricao, apenasAtivos,
						caracteristicasInternacaoOuEmergencia,
						caracteristicaUnidadeExecutora);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String strPesquisa, Boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos,
			boolean caracteristicasInternacaoOuEmergencia,
			boolean caracteristicaUnidadeExecutora) {
		return this.getAghUnidadesFuncionaisRN()
				.pesquisarUnidadeFuncionalPorCodigoEDescricao(strPesquisa,
						ordernarPorCodigoAlaDescricao, apenasAtivos,
						caracteristicasInternacaoOuEmergencia,
						caracteristicaUnidadeExecutora);
	}

	@Override
	public Integer pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(
			String strPesquisa) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisaAghUnidadesFuncionais(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short codigo, String descricao, String sigla,
			AghClinicas clinica, FccCentroCustos centroCusto,
			AghUnidadesFuncionais unidadeFuncionalPai,
			DominioSituacao situacao, String andar, AghAla ala) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisa(firstResult, maxResults, orderProperty, asc, codigo,
						descricao, sigla, clinica, centroCusto,
						unidadeFuncionalPai, situacao, andar, ala);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesPaiPorDescricao(
			Object parametro) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesPaiPorDescricao(parametro);
	}
	
	@Override
	public Long pesquisarUnidadesPaiPorDescricaoCount(String param) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesPaiPorDescricaoCount(param);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadesPorCodigoDescricao(
			Object parametro, boolean orderDescricao) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesPorCodigoDescricao(parametro, orderDescricao);
	}
	
	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidadesPorCodigoDescricaoFilha(
			Object parametro, boolean orderDescricao, Short unidadeFuncionalFilha) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesPorCodigoDescricaoFilha(parametro, orderDescricao, unidadeFuncionalFilha);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisaPacienteInternado(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigo, boolean situacao) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaPacienteInternado(
				firstResult, maxResult, orderProperty, asc, codigo, situacao);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisaAtendimentoUrgencia(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short codigo, boolean situacao) {
		return this.getAghUnidadesFuncionaisDAO().pesquisaAtendimentoUrgencia(
				firstResult, maxResult, orderProperty, asc, codigo, situacao);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais() {
		return this.getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionais();
	}

	@Override
	public Long pesquisarOrigemEventosCount(final Short seqOrigemInternacao,
			final String descricaoOrigemInternacao) {
		return getAghOrigemEventosDAO().pesquisarOrigemEventosCount(
				seqOrigemInternacao, descricaoOrigemInternacao);
	}

	@Override
	public List<AghOrigemEventos> pesquisarOrigemEventos(
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc,
			final Short seqOrigemInternacao,
			final String descricaoOrigemInternacao) {
		return getAghOrigemEventosDAO().pesquisarOrigemEventos(firstResult,
				maxResults, orderProperty, asc, seqOrigemInternacao,
				descricaoOrigemInternacao);
	}

	@Override
	public AghOrigemEventos obterOrigemEventos(final Short seqOrigemInternacao) {
		return getAghOrigemEventosDAO().obterOrigemEventos(seqOrigemInternacao);
	}

	@Override
	public void persistirAghOrigemEventos(AghOrigemEventos aghOrigemEventos) {
		this.getAghOrigemEventosDAO().persistir(aghOrigemEventos);
	}

	@Override
	public void atualizarAghOrigemEventos(AghOrigemEventos aghOrigemEventos) {
		this.getAghOrigemEventosDAO().atualizar(aghOrigemEventos);
	}

	@Override
	public void excluirAghOrigemEventos(AghOrigemEventos aghOrigemEventos) {
		this.getAghOrigemEventosDAO().remover(aghOrigemEventos);
	}

	@Override
	public List<AghOrigemEventos> pesquisarOrigemEventoPorCodigoEDescricao(
			Object objPesquisa) {
		return this.getAghOrigemEventosDAO()
				.pesquisarOrigemEventoPorCodigoEDescricao(objPesquisa);
	}

	/**
	 * Retorna uma Instituição Hospitalar com base na chave primária.
	 * 
	 * @param seq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('instituicaoHospitalar','pesquisar')}")
	public AghInstituicoesHospitalares obterInstituicaoHospitalarPorChavePrimaria(
			final Integer seq) {
		return this.getAghInstituicoesHospitalaresDAO().obterPorChavePrimaria(
				seq);

	}

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param codCidade
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('instituicaoHospitalar','pesquisar')}")
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalar(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final Integer codigo, final String descricao,
			final Integer codCidade) {

		return this.getAghInstituicoesHospitalaresDAO().pesquisarInstituicao(
				firstResult, maxResult, orderProperty, asc, codigo, descricao,
				codCidade);
	}

	/**
	 * 
	 * @param codigo
	 * @param descricao
	 * @param codCidade
	 * @return
	 */
	@Override
	public Long obterCountInstituicaoCodigoDescricao(final Integer codigo,
			final String descricao, final Integer codCidade) {
		return this.getAghInstituicoesHospitalaresDAO()
				.obterCountInstituicaoCodigoDescricao(codigo, descricao);
	}

	@Override
	public Integer recuperarCnesInstituicaoHospitalarLocal() {
		return this.getAghInstituicoesHospitalaresDAO()
				.recuperarCnesInstituicaoHospitalarLocal();
	}

	@Override
	public AghInstituicoesHospitalares recuperarInstituicaoHospitalarLocal() {
		return this.getAghInstituicoesHospitalaresDAO()
				.recuperarInstituicaoLocal();
	}

	/**
	 * Retorna uma lista de instituições ordenado por descrição
	 * 
	 * @param parametro
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('instituicaoHospitalar','pesquisar')}")
	public List<AghInstituicoesHospitalares> pesquisarInstituicaoHospitalarPorCodigoOuDescricaoOrdenado(
			final Object parametro) {

		List<AghInstituicoesHospitalares> listaResultado;

		listaResultado = this.getAghInstituicoesHospitalaresDAO()
				.pesquisarInstituicaoPorCodigoOuDescricaoOrdenado(parametro);

		return listaResultado;
	}

	@Override
	public void inserirAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares,
			boolean flush) {
		this.getAghInstituicoesHospitalaresDAO().persistir(
				aghInstituicoesHospitalares);
		if (flush) {
			this.getAghInstituicoesHospitalaresDAO().flush();
		}
	}

	@Override
	public void atualizarAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares,
			boolean flush) {
		this.getAghInstituicoesHospitalaresDAO().atualizar(
				aghInstituicoesHospitalares);
		if (flush) {
			this.getAghInstituicoesHospitalaresDAO().flush();
		}
	}

	@Override
	public void removerAghInstituicoesHospitalares(
			AghInstituicoesHospitalares aghInstituicoesHospitalares,
			boolean flush) {
		aghInstituicoesHospitalares = this.getAghInstituicoesHospitalaresDAO().obterPorChavePrimaria(aghInstituicoesHospitalares.getSeq());
		this.getAghInstituicoesHospitalaresDAO().remover(
				aghInstituicoesHospitalares);
		if (flush) {
			this.getAghInstituicoesHospitalaresDAO().flush();
		}
	}

	@Override
	public String recuperarNomeInstituicaoLocal() {
		return this.getAghInstituicoesHospitalaresDAO()
				.recuperarNomeInstituicaoLocal();
	}

	@Override
	public AghInstituicoesHospitalares verificarInstituicaoLocal(Integer seq) {
		return this.getAghInstituicoesHospitalaresDAO()
				.verificarInstituicaoLocal(seq);
	}

	@Override
	public AghImpressoraPadraoUnids obterAghImpressoraPadraoUnidsPorChavePrimaria(
			AghImpressoraPadraoUnidsId id) {
		return this.getAghImpressoraPadraoUnidsDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void removerAghImpressoraPadraoUnids(
			AghImpressoraPadraoUnids aghImpressoraPadraoUnids, boolean flush) {
		this.getAghImpressoraPadraoUnidsDAO().remover(aghImpressoraPadraoUnids);
		if (flush) {
			this.getAghImpressoraPadraoUnidsDAO().flush();
		}
	}

	@Override
	public Long pesquisaImpressorasCount(Short unfSeq) {
		return this.getAghImpressoraPadraoUnidsDAO().pesquisaImpressorasCount(
				unfSeq);
	}

	@Override
	public List<ImpImpressora> listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(
			Short unfSeq, TipoDocumentoImpressao tipoImpressora) {
		return this.getAghImpressoraPadraoUnidsDAO()
				.listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(unfSeq,
						tipoImpressora);
	}

	@Override
	public List<AghImpressoraPadraoUnids> obterAghImpressoraPadraoUnids(
			Short seq) {
		return this.getAghImpressoraPadraoUnidsDAO()
				.obterAghImpressoraPadraoUnids(seq);
	}

	@Override
	public void inserirAghImpressoraPadraoUnids(
			AghImpressoraPadraoUnids aghImpressoraPadraoUnids, boolean flush) {
		this.getAghImpressoraPadraoUnidsDAO().persistir(
				aghImpressoraPadraoUnids);
		if (flush) {
			this.getAghImpressoraPadraoUnidsDAO().flush();
		}
	}

	/**
	 * @dbtables AghTiposUnidadeFuncional select
	 * @param aghTiposUnidadeFuncionalCodigo
	 * @return
	 */
	@Override
	public AghTiposUnidadeFuncional obterTiposUnidadeFuncional(
			final Integer aghTiposUnidadeFuncionalCodigo) {
		return this.getAghTiposUnidadeFuncionalDAO()
				.obterTiposUnidadeFuncional(aghTiposUnidadeFuncionalCodigo);
	}

	@Override
	public Long pesquisaTiposUnidadeFuncionalCount(Integer codigo,
			String descricao) {
		return this.getAghTiposUnidadeFuncionalDAO()
				.pesquisaTiposUnidadeFuncionalCount(codigo, descricao);
	}

	@Override
	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao) {
		return this.getAghTiposUnidadeFuncionalDAO()
				.pesquisaTiposUnidadeFuncional(firstResult, maxResults,
						orderProperty, asc, codigo, descricao);
	}

	@Override
	public List<AghTiposUnidadeFuncional> pesquisaTiposUnidadeFuncional(
			String descricao) {
		return this.getAghTiposUnidadeFuncionalDAO()
				.pesquisaTiposUnidadeFuncional(descricao);
	}

	@Override
	public List<AghTiposUnidadeFuncional> listarPorNomeOuCodigo(
			String strPesquisa) {
		return this.getAghTiposUnidadeFuncionalDAO().listarPorNomeOuCodigo(
				strPesquisa);
	}

	@Override
	public boolean validarTipoUnidadeFuncionalComUnidadeFuncional(
			AghTiposUnidadeFuncional tiposUnidadeFuncional) {
		return this.getAghTiposUnidadeFuncionalDAO()
				.validarTipoUnidadeFuncionalComUnidadeFuncional(
						tiposUnidadeFuncional);
	}

	@Override
	public void inserirAghTiposUnidadeFuncional(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional, boolean flush) {
		this.getAghTiposUnidadeFuncionalDAO().persistir(aghTiposUnidadeFuncional);
		if (flush){
			this.getAghTiposUnidadeFuncionalDAO().flush();
		}
	}

	@Override
	public void atualizarAghTiposUnidadeFuncionalDepreciado(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional) {
		this.getAghTiposUnidadeFuncionalDAO().atualizar(
				aghTiposUnidadeFuncional);
	}

	@Override
	public void removerAghTiposUnidadeFuncional(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional) {
		this.getAghTiposUnidadeFuncionalDAO().remover(aghTiposUnidadeFuncional);
		this.getAghTiposUnidadeFuncionalDAO().flush();
	}

	@Override
	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(
			Short seq, DominioCaracEspecialidade caracteristica) {
		return this.getAghCaractEspecialidadesDAO()
				.pesquisarCaracteristicaEspecialidade(seq, caracteristica);
	}

	@Override
	public List<AghAtendimentoPacientes> listarAtendimentosPacientes(
			Integer pacCodigo, Integer seqAtendimento) {
		return this.getAghAtendimentoPacientesDAO()
				.listarAtendimentosPacientes(pacCodigo, seqAtendimento);
	}
	
	@Override
	public List<AghAtendimentos> buscarAtendimentosPaciente(Integer pacCodigo, Integer atd) {
		return this.getAghAtendimentoDAO().buscarAtendimentosPaciente(pacCodigo, atd);
	}

	protected AghCaractUnidFuncionaisJnDAO getAghCaractUnidFuncionaisJnDAO() {
		return aghCaractUnidFuncionaisJnDAO;
	}

	protected AghUnidadesFuncionaisJnDAO getAghUnidadesFuncionaisJnDAO() {
		return aghUnidadesFuncionaisJnDAO;
	}

	protected AghEspecialidadesJnDAO getAghEspecialidadesJnDAO() {
		return aghEspecialidadesJnDAO;
	}

	protected AghTiposUnidadeFuncionalDAO getAghTiposUnidadeFuncionalDAO() {
		return aghTiposUnidadeFuncionalDAO;
	}

	protected AghEquipesDAO getAghEquipesDAO() {
		return aghEquipesDAO;
	}

	@Override
	public List<AghEspecialidades> pesquisarPorSiglaIndices(String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorSiglaIndices(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesPorServidor(final RapServidores servidor) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesPorServidor(servidor);
	}	

	@Override
	public List<AghProfEspecialidades> pesquisarConsultoresPorEspecialidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final AghEspecialidades especialidade) {
		return getAghProfEspecialidadesDAO().pesquisarConsultoresPorEspecialidade(
				firstResult, maxResult, orderProperty, asc, especialidade);
	}
	@Override
	public Long pesquisarConsultoresPorEspecialidadeCount(
			final AghEspecialidades especialidade) {
		return getAghProfEspecialidadesDAO()
				.pesquisarConsultoresPorEspecialidadeCount(especialidade);
	}	

	@Override
	public List<AghEspecialidades> pesquisarPorNomeIndices(String parametro) {
		return getAghEspecialidadesDAO().pesquisarPorSiglaIndices(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesPorInternacao(
			Integer atdSeq, Short espSeq, Integer intSeq)
			throws ApplicationBusinessException {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesPorInternacao(
				atdSeq, espSeq, intSeq);
	}

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesPorAtendUrgencia(
			Integer atdSeq, Short espSeq, Integer atuSeq)
			throws ApplicationBusinessException {
		return getAghEspecialidadesDAO()
				.pesquisarEspecialidadesPorAtendUrgencia(atdSeq, espSeq, atuSeq);
	}

	@Override
	public List<AghEspecialidades> pesquisarPorIdSigla(Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorIdSigla(parametro);
	}
	
	@Override
	public Long pesquisarPorIdSiglaCount(Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorIdSiglaCount(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarPorNomeIdSiglaIndices(
			Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeIdSiglaIndices(parametro);
	}
	
	@Override
	public Long pesquisarPorNomeIdSiglaIndicesCount(Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorNomeIdSiglaIndicesCount(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarPorSiglaAtivas(Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorSiglaAtivas(parametro);
	}

	@Override
	public List<AghEspecialidades> pesquisarPorSiglaAtivasIndices(
			Object parametro) {
		return getAghEspecialidadesDAO().pesquisarPorSiglaAtivasIndices(
				parametro);
	}

	@Override
	public Integer obterPrimeiroAtendimentoDeContasInternacaoPorContaHospitalar(
			Integer pCthSeq) {
		return getAghAtendimentoDAO()
				.obterPrimeiroAtendimentoDeContasInternacaoPorContaHospitalar(
						pCthSeq);
	}

	@Override
	public AghAtendimentos buscarAtendimentoContaParto(Integer cthSeq) {
		return getAghAtendimentoDAO().buscarAtendimentoContaParto(cthSeq);
	}

	@Override
	public Long verificaBebeInternadoCount(Integer vAtdSeq) {
		return getAghAtendimentoDAO().verificaBebeInternadoCount(vAtdSeq);
	}

	@Override
	public Integer verificaSSMPartoCount(Integer cthSeq, Long[] codigosTabela) {
		return getAghAtendimentoDAO().verificaSSMPartoCount(cthSeq,
				codigosTabela);
	}

	@Override
	public DominioRNClassificacaoNascimento verificarAbo(final Integer pIntSeq){
		return getAghAtendimentoDAO().verificarAbo(pIntSeq);
	}

	@Override
	public DadosRegistroCivilVO obterDadosRegistroCivil(final Integer pIntSeq){
		return getAghAtendimentoDAO().obterDadosRegistroCivil(pIntSeq);
	}

	@Override
	public String buscaCodigoCidSecundarioConta(Integer cthSeq,
			DominioPrioridadeCid prioridade) {
		return getAghCidsDAO().buscaCodigoCidSecundarioConta(cthSeq, prioridade);
	}

	@Override
	@Secure("#{s:hasPermission('cidPorEquipeConsulta','pesquisar')}")
	public List<AghCid> pesquisarPorNomeCodigoAtiva(String parametro) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtiva(parametro);
	}
	
	@Override
	public Long pesquisarPorNomeCodigoAtivaCount(String parametro) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaCount(parametro);
	}

	@Override
	public AghAtendimentoPacientes obterAghAtendimentoPacientesPorChavePrimaria(
			AghAtendimentoPacientesId id) {
		return getAghAtendimentoPacientesDAO().obterPorChavePrimaria(id);
	}

	/**
	 * Retorna true se a unidade funcional possui a caracteristica fornecida.
	 * 
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public boolean validarCaracteristicaDaUnidadeFuncional(final Short unfSeq,
			final ConstanteAghCaractUnidFuncionais caracteristica) {
		return this.getAghCaractUnidFuncionaisDAO()
				.verificarCaracteristicaDaUnidadeFuncional(unfSeq,
						caracteristica) == DominioSimNao.S;
	}
	
	/**
	 * Retorna uma map com os AghCaractUnidFuncionais filtradas por atendimentos e caracteristica.<br>
	 * Retorna true se a unidade funcional do Atendimento possui a caracteristica fornecida.<br>
	 * Key do Map eh formada da seguinte forma: <b>unfSeq:caracteristica</b>
	 * 
	 * @param atdSeqs
	 * @param caracteristica
	 * @return
	 */
	@Override
	@BypassInactiveModule
	public Map<String, AghCaractUnidFuncionais> validarCaracteristicaDaUnidadeFuncional(final List<Integer> atdSeqs, final ConstanteAghCaractUnidFuncionais caracteristica) {
		Map<String, AghCaractUnidFuncionais> mapCaractUnidFuncionais = new HashMap<String, AghCaractUnidFuncionais>();
		
		List<AghCaractUnidFuncionais> list = this.getAghCaractUnidFuncionaisDAO().verificarCaracteristicaDaUnidadeFuncional(atdSeqs, caracteristica);
		for (AghCaractUnidFuncionais aghCaractUnidFuncional : list) {
			String key = aghCaractUnidFuncional.getId().getUnfSeq() + ":" + aghCaractUnidFuncional.getId().getCaracteristica().getCodigo();
			mapCaractUnidFuncionais.put(key, aghCaractUnidFuncional);
		}//FOR AghCaractUnidFuncionais
		
		return mapCaractUnidFuncionais;
	}

	@Override
	public List<AghCid> obterCidPorNomeCodigoAtivaPaginado(String param) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaPaginado(param);
	}

	/**
	 * @author lucasbuzzo #3476
	 * 
	 * @param Integer
	 *            param
	 * @return AghCid
	 */
	@Override
	public AghCid obterCidUnico(Integer param) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaPaginadoUnico(param);
	}

	@Override
	public void refreshUnidadesFuncionais(AghUnidadesFuncionais unidade) {
		getAghUnidadesFuncionaisDAO().refresh(unidade);
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadeExecutora(Object param) {
		return getAghUnidadesFuncionaisDAO().obterUnidadesExecutora(param);
	}
	
	@Override
	public Long listarUnidadeExecutoraCount(Object param) {
		return getAghUnidadesFuncionaisDAO().obterUnidadesExecutoraCount(param);
	}

	@Override
	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorParametro(
			String valorParam) {
		return getAghUnidadesFuncionaisDAO()
				.buscarAghUnidadesFuncionaisPorParametro(valorParam);
	}

	@Override
	public Long pesquisarAtendimentoParaSolicitacaoExamesCount(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente) {
		return getAghAtendimentoDAO().pesquisarAtendimentoParaSolicitacaoExamesCount(filter,fonemasPaciente);
	}

	@Override
	public Long pesquisarAtendimentosPacienteInternadoCount(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente) {
		return getAghAtendimentoDAO().pesquisarAtendimentosPacienteInternadoCount(filter,fonemasPaciente);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoParaSolicitacaoExames(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoParaSolicitacaoExames(filter,
						fonemasPaciente, firstResult, maxResult, orderProperty,
						asc);

	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosPacientesInternados(
			SolicitacaoExameFilter filter, List<String> fonemasPaciente,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentosPacientesInternados(filter,
						fonemasPaciente, firstResult, maxResult, orderProperty,
						asc);

	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisPorSequencialOuDescricao(
			String parametro) {
		return this.getAghUnidadesFuncionaisDAO()
				.pesquisarPorSequencialOuDescricao(parametro);
	}
	

	@Override
	public Long pesquisarAghUnidadesFuncionaisPorSequencialOuDescricaoCount(String parametro) {
		return this.getAghUnidadesFuncionaisDAO().pesquisarPorSequencialOuDescricaoCount(parametro);
	}	
	

	@Override
	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisPorCodigoDescricao(final Object parametro, final boolean orderDescricao) {
		return this.getAghUnidadesFuncionaisDAO().pesquisarAghUnidadesFuncionaisPorCodigoDescricao(parametro, orderDescricao);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(
			Integer numeroConsulta, Date dthrRealizado) {
		return getAghAtendimentoDAO()
				.pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(
						numeroConsulta, dthrRealizado);
	}

	@Override
	public String obterCidExamePorItemSolicitacaoExames(Integer iseSoeSeq,
			Short iseSeqp, Integer qquQaoSeq) {
		return getAghCidsDAO().obterCidExamePorItemSolicitacaoExames(iseSoeSeq,
				iseSeqp, qquQaoSeq);
	}

	@Override
	public String obterCodigoAghCidsPorPhiSeq(Integer phiSeq) {
		return getAghCidsDAO().obterCodigoAghCidsPorPhiSeq(phiSeq);
	}

	@Override
	public DominioSexoDeterminante obterRestricaoSexo(String codigoCid) {
		return getAghCidsDAO().obterRestricaoSexo(codigoCid);
	}

	@Override
	public Integer buscaCidPorCodigoComPonto(String codigo) {
		return getAghCidsDAO().buscaCidPorCodigoComPonto(codigo);
	}

	@Override
	public AghFeriados obterFeriadoSemTurno(Date dtExecucao) {
		return getAghFeriadosDAO().obterFeriadoSemTurno(dtExecucao);
	}

	@Override
	public List<ConsultaRateioProfissionalVO> listarConsultaRateioServicosProfissionais(
			FatCompetencia competencia) throws ApplicationBusinessException {
		return getAghEspecialidadesDAO()
				.listarConsultaRateioServicosProfissionais(competencia);
	}

	@Override
	public Long obterQtdFeriadosEntreDatas(Date inicio, Date fim) {
		return getAghFeriadosDAO().obterQtdFeriadosEntreDatas(inicio, fim);
	}

	

	@Override
	public BigDecimal capacidadeProf(Short pEsp, Short pVin, Integer pMatr) {
		return getAghProfEspecialidadesDAO().capacidadeProf(pEsp, pVin, pMatr);
	}

	@Override
	public List<Object> obterCapacUnid() {
		return getAghUnidadesFuncionaisDAO().obterCapacUnid();
	}

	@Override
	public List<AghEspecialidades> carregarCapacidadeEspecialidade() {
		return getAghEspecialidadesDAO().carregarCapacidadeEspecialidade();
	}

	@Override
	public List<AghClinicas> obterTodasClinicas() {
		return getAghClinicasDAO().obterTodasClinicas();
	}

	@Override
	public List<Short> listarUnidadesFuncionaisPorUnfSeq(List<Short> listUnfSeq) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorUnfSeq(
				listUnfSeq);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorAtendimentoPacienteExterno(
			AghAtendimentosPacExtern atendimentoPacExterno) {
		return getAghAtendimentoDAO().listarPorAtendimentoPacienteExterno(
				atendimentoPacExterno);
	}
	
	@Override
	public void finalizarAghArquivoProcessamento(List<Integer> seqs) {
		this.getAghArquivoProcessamentoDAO().finalizarAghArquivoProcessamento(seqs);
		this.getAghArquivoProcessamentoDAO().flush();
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosComInternacaoEmAndamentoPorPaciente(
			Integer codigo) {
		return getAghAtendimentoDAO()
				.listarAtendimentosComInternacaoEmAndamentoPorPaciente(codigo);
	}

	@Override
	public void inserirCentroObstetrico(Integer pAtdSeq, Integer pCth,
			Date pDtInter, Date pDtAlta, Integer pOpcao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getAghAtendimentoDAO().inserirCentroObstetrico(pAtdSeq, pCth,
				pDtInter, pDtAlta, pOpcao, servidorLogado.getUsuario());
	}

	@Override
	public AghAtendimentos obterAtendimentoPorTriagem(Long trgSeq) {
		return getAghAtendimentoDAO().obterAtendimentoPorTriagem(trgSeq);
	}

	@Override
	public List<AghAtendimentos> verificarSePacienteTemAtendimento(
			Integer pacCodigo, Integer numDiasPassado, Integer numDiasFuturo) {
		return getAghAtendimentoDAO().verificarSePacienteTemAtendimento(
				pacCodigo, numDiasPassado, numDiasFuturo);
	}

	@Override
	public List<AghAtendimentos> buscarPacienteInternado(Integer pacCodigo) {
		return getAghAtendimentoDAO().buscarPacienteInternado(pacCodigo);
	}

	@Override
	public List<AghAtendimentos> buscarPacientesAlta(Integer pacCodigo,
			Integer pNroDiasPas) {
		return getAghAtendimentoDAO().buscarPacientesAlta(pacCodigo,
				pNroDiasPas);
	}

	@Override
	public List<NodoAtendimentoEmergenciaPOLVO> pesquisarNodosMenuAtendimentosEmergenciaPol(
			Integer codPaciente) {
		return getAghAtendimentoDAO()
				.pesquisarNodosMenuAtendimentosEmergenciaPol(codPaciente);
	}
	
	@Override
	public AghNodoPol obterAghNodoPolPorNome(String nome) {
		return getAghNodoPolDAO().obterAghNodoPolPorNome(nome);
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadeFuncionalComSala(
			String parametro) {
		return getAghUnidadesFuncionaisDAO().listarUnidadeFuncionalComSala(
				parametro);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarConsultasPaciente(
			Integer pacCodigo, Integer nroDiasFuturo, Integer nroDiasPassado, Integer paramReteronoConsAgendada) {
		return getAghUnidadesFuncionaisDAO().pesquisarConsultasPaciente(
				pacCodigo, nroDiasFuturo, nroDiasPassado,paramReteronoConsAgendada);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPaciente(
			DominioPacAtendimento indpacAtendimento, Integer nroDias,
			Integer pacCodigo, Boolean centroDeCustoNotNull) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPaciente(indpacAtendimento,
						nroDias, pacCodigo, centroDeCustoNotNull);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarCirurgiasUnidadesFuncionais(
			Integer pacCodigo, Integer nroDiasFuturo, Integer nroDiasPassado) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarCirurgiasUnidadesFuncionais(pacCodigo, nroDiasFuturo,
						nroDiasPassado);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(
			String strPesquisa) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoDescricaoAndar(strPesquisa);
	}

	@Override
	public AghEspecialidades obterEspecialidadePorChavePrimaria(Short seq) {
		return this.getAghEspecialidadesDAO().obterPorChavePrimaria(seq);
	}

	/*
	@Override
	public Integer getAghGrupoCidsNextVal(SequenceID id) {
		return getAghGrupoCidsDAO().getNextVal(id);
	}
	*/

	@Override
	public List<Short> obterUnidadesFuncionaisHierarquicasPorCaractCentralRecebimento(
			Short seq) {
		return this
				.getAghUnidadesFuncionaisDAO()
				.obterUnidadesFuncionaisHierarquicasPorCaractCentralRecebimento(
						seq);
	}

	@Override
	public List<AghAtendimentos> pesquisarAghAtendimentosPorPacienteEConsulta(
			Integer codPaciente, Integer numConsulta) {
		return getAghAtendimentoDAO()
				.pesquisarAghAtendimentosPorPacienteEConsulta(codPaciente,
						numConsulta);
	}

	@Override
	public AghEspecialidades obterEspecialidadePorServidor(Integer matricula,
			Short vinCodigo) {
		return this.getAghEspecialidadesDAO().obterEspecialidadePorServidor(
				matricula, vinCodigo);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorCodigoPacienteNumeroConsulta(
			Integer pPacCodigo, Integer pConNumero) {
		return getAghAtendimentoDAO()
				.obterAtendimentoPorCodigoPacienteNumeroConsulta(pPacCodigo,
						pConNumero);
	}

	@Override
	public List<AghAtendimentos> listarAtendimentosPorPacCodGestacaoSeq(
			Integer gsoPacCodigo, Short gsoSeqp, Integer pepPacCodigo) {
		return getAghAtendimentoDAO().listarAtendimentosPorPacCodGestacaoSeq(
				gsoPacCodigo, gsoSeqp, pepPacCodigo);
	}

	@Override
	public AghCid obterAghCidPorSeq(Integer seqCid) {
		return getCidON().obterCid(seqCid);
	}

	@Override
	public AghAtendimentos obterAtendimentoComUnidadeFuncional(Integer atdSeq) {
		return getAghAtendimentoDAO().obterAtendimentoComUnidadeFuncional(
				atdSeq);
	}

	@Override
	public List<AghAtendimentos> obterAtendimentosPorPacienteEOrigemOrdenadosPeloAtdExt(DominioPacAtendimento pacAtendimento, Integer pacCodigo, List<DominioOrigemAtendimento> origens) {
		return getAghAtendimentoDAO().obterAtendimentosPorPacienteEOrigemOrdenadosPeloAtdExt(pacAtendimento, pacCodigo, origens);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPorPacienteEOrigem(Integer pacCodigo) {
		return getAghAtendimentoDAO().obterAtendimentoPorPacienteEOrigem(pacCodigo);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPorPacienteEDthr(Integer pacCodigo, Date dthrFim) {
		return getAghAtendimentoDAO().obterAtendimentoPorPacienteEDthr(pacCodigo,dthrFim);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPorPacienteEOrigemDthrFim(Integer pacCodigo, DominioOrigemAtendimento origem, Date dthrFimCirg) {
		return getAghAtendimentoDAO().obterAtendimentoPorPacienteEOrigemDthrFim(pacCodigo, origem, dthrFimCirg);
	}

	public Integer obterSeqAghAtendimentoPorPaciente(final Integer pacCodigo, final DominioOrigemAtendimento[] origens, final DominioPacAtendimento indPacAtendimento) {
		return getAghAtendimentoDAO().obterSeqAghAtendimentoPorPaciente(pacCodigo, origens, indPacAtendimento);
	}
	
	@Override
	public SumarioQuimioPOLVO listarDadosCabecalhoSumarioPrescricaoQuimio(
			Integer atdSeq, Integer apaSeq) {
		return getAghAtendimentoPacientesDAO()
				.listarDadosCabecalhoSumarioPrescricaoQuimio(atdSeq, apaSeq);
	}

	@Override
	public List<AelItemSolicitacaoExames> listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(
			Integer seq, String sitCodigoLi, String sitCodigoAe) {
		return getAghAtendimentoDAO()
				.listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(seq,
						sitCodigoLi, sitCodigoAe);
	}
	
	public List<AelItemSolicitacaoExames> listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo( final String sitCodigoLi, final String sitCodigoAe,
																						 	final Integer ppeSeqp,  final Integer ppePleSeq,
																						 	final Integer asuApaAtdSeq, final Integer asuApaSeq,
																						 	final Short apeSeqp){
		return getAghAtendimentoDAO().listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(sitCodigoLi, sitCodigoAe, ppeSeqp, 
																					   ppePleSeq, asuApaAtdSeq, asuApaSeq, apeSeqp);
		
	}

	@Override
	public List<AghCaixaPostalAplicacao> pesquisarCaixaPostalPorCxtSeq(
			final Long cxtSeq) {
		return this.getAghCaixaPostalAplicacaoDAO()
				.pesquisaCaixaPostalPorCxtSeq(cxtSeq);
	}
	
	@Override
	public Date buscaDataHoraBancoDeDados() {
		return getAghuConfigRN().buscaDataHoraBancoDeDados();
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqSigla(Object parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqSigla(parametro);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorDescricao(Object parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorDescricao(parametro);
	}

	@Override
	public Long pesquisarUnidadeFuncionalPorSeqSiglaCount(Object parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqSiglaCount(parametro);
	}

	@Override
	public Long pesquisarUnidadeFuncionalPorDescricaoCount(Object parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorDescricaoCount(parametro);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqDescricao(String parametro, Boolean somenteAtivos) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqDescricao(parametro, somenteAtivos);
	}
	
    @Override
    public Long pesquisarUnidadeFuncionalPorSeqDescricaoCount(String parametro, Boolean somenteAtivos) {
        return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqDescricaoCount(parametro, somenteAtivos);
    }
	
	@Override
	public void persistirDocumentoContigencia(AghDocumentoContingencia documentoContigencia) throws BaseException{
		this.getDocumentoContingenciaON().salvarDocumentoContigencia(documentoContigencia);
	}
	
	private DocumentoContingenciaON getDocumentoContingenciaON() {
		return documentoContingenciaON;
		
	}

	@Override
	public List<AghCid> buscarCidsPorExameMaterial(final String sigla, final Integer manSeq) {
		return getAghCidsDAO().buscarCidsPorExameMaterial(sigla, manSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AghCid> pesquisarCidsPorDescricaoOuId(final String descricao, final Integer limiteRegistros, final DominioSexoDeterminante sexoPac, final String sigla, final Integer manSeq) {
		return getAghCidsDAO().pesquisarCidsPorDescricaoOuId(descricao, limiteRegistros, sexoPac, sigla, manSeq);
	}

	@Override
	@BypassInactiveModule
	public Long contarCidsPorDescricaoOuId(String descricao, DominioSexoDeterminante sexoPac){
		return getAghCidsDAO().contarCidsPorDescricaoOuId(descricao, sexoPac);
	}
	
	@Override
	@BypassInactiveModule
	public List<AghCid> pesquisarCidsPorDescricaoOuIdPassandoExame(final String descricao, final Integer limiteRegistros, final DominioSexoDeterminante sexoPac, final String sigla, final Integer manSeq) {
		return getAghCidsDAO().pesquisarCidsPorDescricaoOuId(descricao, limiteRegistros, sexoPac, sigla, manSeq);
	}
	
	@Override
	@BypassInactiveModule
	public Long contarCidsPorDescricaoOuIdPassandoExame(String descricao, DominioSexoDeterminante sexoPac, String sigla, Integer manSeq){
		return getAghCidsDAO().contarCidsPorDescricaoOuId(descricao, sexoPac, sigla, manSeq);
	}
	
	@Override
	public AghEquipes obterEquipeNotRestrict(Integer seqEquipe) {
		return this.getAghEquipesDAO().obterPorChavePrimaria(seqEquipe);
	}

	@Override
	public List<VAghUnidFuncional> obterUnidFuncionalPorCaracteristicaInternacaoOuEmergencia(Object parametro) {
		return getVAghUnidFuncionalDAO().obterUnidFuncionalPorCaracteristicaInternacaoOuEmergencia(parametro);
	}
	
	@Override
	public List<LocalPacienteVO> pesquisarUnidFuncionalPorCaracteristica(String param) {
		return getVAghUnidFuncionalDAO().pesquisarUnidFuncionalPorCaracteristica(param);
	}
	
	@Override
	public Long pesquisarUnidFuncionalPorCaracteristicaCount(String param) {
		return getVAghUnidFuncionalDAO().pesquisarUnidFuncionalPorCaracteristicaCount(param);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(String filtro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisPorCodigoDescricao(filtro);
	}
	
    @Override
    public Long pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(Object filtro) {
        return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro);
    }
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(Object filtro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(filtro, null);
	}
	
	@Override
	public Long pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgiasCount(Object filtro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgiasCount(filtro, null);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisAtivasPorCaracteristica(final ConstanteAghCaractUnidFuncionais caractUnidFuncional) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisAtivasPorCaracteristica(caractUnidFuncional);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisExecutoraCirurgias(Object filtro, Boolean asc, String order) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisExecutoraCirurgias(filtro, asc, order);
	}

	@Override
	public Long pesquisarUnidadesFuncionaisExecutoraCirurgiasCount(Object strPesquisa) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesFuncionaisExecutoraCirurgiasCount(strPesquisa);
	}

	@Override
	public Long pesquisarEspecialidadesPorNomeOuSiglaCount(String paramPesquisa) {
		return getAghEspecialidadesDAO().pesquisarPorNomeOuSiglaCount(paramPesquisa);
	}
	
	@Override
	public Short obterMenorTempoMinimoUnidadeFuncionalCirurgia(ConstanteAghCaractUnidFuncionais caractUnidFuncional) {
		return getAghUnidadesFuncionaisDAO().obterMenorTempoMinimoUnidadeFuncionalCirurgia(caractUnidFuncional);
	}

	@Override
	public List<Integer> listarSeqAtdPorOrigem(Integer pacCodigo, DominioOrigemAtendimento[] origens){
		return getAghAtendimentoDAO().listarSeqAtdPorOrigem(pacCodigo, origens);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaExata(String parametro) {
		return this.getEspecialidadeON().pesquisarPorNomeOuSiglaExata(parametro);

	}

	@Override
	public AghFeriados obterFeriadoSemTurnoDataTruncada(Date dataCirurgia) {
		return getAghFeriadosDAO().obterFeriadoSemTurnoDataTruncada(dataCirurgia);
	}
	
	@Override
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorCaracteristica(String filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorCaracteristica(filtro, constanteAghCaractUnidFuncionais);
	}
	
	@Override
	public Long listarUnidadesFuncionaisPorCaracteristicaCount(String filtro, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorCaracteristicaCount(filtro, constanteAghCaractUnidFuncionais);
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(
			String strPesquisa,
			ConstanteAghCaractUnidFuncionais unidExecutoraCirurgias, Boolean somenteAtivos) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorUnidadeExecutora(strPesquisa, unidExecutoraCirurgias, somenteAtivos);
	}

	@Override
	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(
			String strPesquisa,
			ConstanteAghCaractUnidFuncionais unidExecutoraCirurgias, Boolean somenteAtivos) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa, unidExecutoraCirurgias, somenteAtivos);
	}

	@Override
	public List<AghCid> pesquisarCidPorCodigoDescricao(final Integer maxResult, final Object objParam) {
		return getAghCidsDAO().pesquisarCidPorCodigoDescricao(maxResult, objParam);
	}
	
	@Override
	public Long pesquisarCidPorCodigoDescricaoCount(final Object objParam){
		return getAghCidsDAO().pesquisarCidPorCodigoDescricaoCount(objParam);
	}

	@Override
	public List<AghCid> pesquisarCidAtivosUsuaisEquipe(final Integer matriculaEquipe, final Short vinCodigoEquipe) {
		return getAghCidsDAO().pesquisarCidAtivosUsuaisEquipe(matriculaEquipe, vinCodigoEquipe);
	}

	@Override
	public Long listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(
			String strPesquisa, ConstanteAghCaractUnidFuncionais unidInternacao, Boolean ativo) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorSeqDescricaoAndarAlaCount(strPesquisa, unidInternacao, ativo);
	}

	@Override
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorSeqDescricaoAndarAla(
			String strPesquisa, ConstanteAghCaractUnidFuncionais unidInternacao, Boolean ativo) {
		return getAghUnidadesFuncionaisDAO().listarUnidadesFuncionaisPorSeqDescricaoAndarAla(strPesquisa, unidInternacao, ativo);
	}
	
	@Override
	public List<AghCid> listarAghCidPorCodigoDescricao(
			String codigoOuDescricao, Integer seq, DominioSituacao situacao,
			boolean somenteCodigo, boolean somenteDescricao,
			Integer firstResult, Integer maxResults, boolean asc,
			AghCid.Fields... ordersProperty) {
		return getAghCidsDAO().listarAghCidPorCodigoDescricao(
				codigoOuDescricao, seq, situacao, somenteCodigo,
				somenteDescricao, firstResult, maxResults, asc, ordersProperty);
	}

	@Override
	public Long listarAghCidPorCodigoDescricaoCount(
			String codigoOuDescricao, Integer seq, DominioSituacao situacao,
			boolean somenteCodigo, boolean somenteDescricao) {
		return getAghCidsDAO().listarAghCidPorCodigoDescricaoCount(
				codigoOuDescricao, seq, situacao, somenteCodigo,
				somenteDescricao);
	}

	@Override
	public AghCid obterAghCidPorChavePrimaria(Integer seq) {
		return getAghCidsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesExecutorasCirurgias(String objPesquisa) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesExecutorasCirurgias(objPesquisa);
	}

	@Override
	public Long pesquisarUnidadesExecutorasCirurgiasCount(String objPesquisa) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesExecutorasCirurgiasCount(objPesquisa);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisaUnidadesFilhasVinculadas(final Short unidadeFuncionalPaiSeq, final DominioSituacao situacao) {
		return getAghUnidadesFuncionaisDAO().pesquisaUnidadesFilhasVinculadas(unidadeFuncionalPaiSeq, situacao);
	}
	
	@Override
	public List<AghAtendimentos> pesquisaAtendimentoPacienteInternadoUrgencia(Integer pacCodigo) {
		return getAghAtendimentoDAO().pesquisaAtendimentoPacienteInternadoUrgencia(pacCodigo);
	}
	
	public List<AghAtendimentos> pesquisaAtendimentoPacientePorOrigem(Integer pacCodigo, DominioOrigemAtendimento origem, Date dthrInicio) {
		return getAghAtendimentoDAO().pesquisaAtendimentoPacientePorOrigem(pacCodigo, origem, dthrInicio);
	}
	
	@Override
	public List<AghAtendimentos> buscaInternacoesDentroIntervaloNaoCalculado(SigProcessamentoCusto processamentoCusto) {
		return getAghAtendimentoDAO().buscaInternacoesDentroIntervaloNaoCalculado(processamentoCusto);
	}
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesSemEspSeq(String strPesquisa) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadesSemEspSeq(strPesquisa);
	}
	
	@Override
	public Long pesquisarEspecialidadesSemEspSeqCount(String strPesquisa) {
		return this.getAghEspecialidadesDAO().pesquisarEspecialidadesSemEspSeqCount(strPesquisa);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPacientePorOrigem(Integer pacCodigo, DominioOrigemAtendimento... origem) {
		return getAghAtendimentoDAO().obterAtendimentoPacientePorOrigem(pacCodigo, origem);
	}

	@Override
	public Integer pesquisarEspecialidadePrincipalAtivaPorNomeOuSiglaCount(String parametro) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadePrincipalAtivaPorNomeOuSiglaCount(parametro);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaEspSeqNulo(String parametro) {
		return this.getAghEspecialidadesDAO().pesquisarPorNomeOuSiglaEspSeqNulo(parametro);
	}
	
	@Override
	public Long pesquisarPorNomeOuSiglaEspSeqNuloCount(String parametro) {
		return this.getAghEspecialidadesDAO().pesquisarPorNomeOuSiglaEspSeqNuloCount(parametro);
	}
	
	@Override
	public AghEspecialidades obterEspecialidadePorEquipe(Integer eqpSeq){
		return getAghEspecialidadesDAO().obterEspecialidadePorEquipe(eqpSeq);
	}
	@Override
	public List<AghProfEspecialidades> pesquisarProfEspecialidadesCirurgiao(Short pEsp, Short pVin, Integer pMatr) {
		return getAghProfEspecialidadesDAO().pesquisarProfEspecialidadesCirurgiao(pEsp, pVin, pMatr);
	}

	public List<AghAtendimentos> pesquisarAtendimentoRegistroCirurgiaRealizada(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg){
		return this.getAghAtendimentoDAO().pesquisarAtendimentoRegistroCirurgiaRealizada(atdSeq, pacCodigo, dthrInicioCirg);
	}
		
	public Integer pesquisarAtendimentoRegistroCirurgiaRealizadaCount(final Integer atdSeq, final Integer pacCodigo, Date dthrInicioCirg){
		return this.getAghAtendimentoDAO().pesquisarAtendimentoRegistroCirurgiaRealizadaCount(atdSeq, pacCodigo, dthrInicioCirg);
	}
	
	@Override
	public List<AghProfEspecialidades> pesquisarEspecialidadeMesmaEspecialidadeCirurgia(Integer crgSeq, Short crgEspMae) {
		return getAghProfEspecialidadesDAO().pesquisarEspecialidadeMesmaEspecialidadeCirurgia(crgSeq, crgEspMae);
	}

	@Override
	public AghAtendimentos buscarDadosPacientePorAtendimento(Integer seqAtendimento, DominioOrigemAtendimento... origem) {
		return getAghAtendimentoDAO().buscarDadosPacientePorAtendimento(seqAtendimento, origem);
	}
	 
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutoraMonitorCirurgia(Object parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeExecutoraMonitorCirurgia(parametro);
	}
	
	@Override
	public List<AghCaractUnidFuncionais> pesquisarCaracteristicaComAtendimentoPorPaciente(Integer pacCodigo, Date dtHrFim,
			List<DominioOrigemAtendimento> origensAtendimento,
			ConstanteAghCaractUnidFuncionais caracteristica) {
		return getAghCaractUnidFuncionaisDAO().pesquisarCaracteristicaComAtendimentoPorPaciente(pacCodigo, dtHrFim, origensAtendimento, caracteristica);
	}
	
	@Override
	public Long obterCidPorNomeCodigoAtivaCount(String param) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaCount(param);
	}
	@Override
	public Integer pesquisarUnidadeExecutoraMonitorCirurgiaCount(Object parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeExecutoraMonitorCirurgiaCount(parametro);
	}

	@Override
	public Long listarCaractUnidFuncionaisEUnidadeFuncionalCount(String objPesquisa, ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais) {
		return getAghCaractUnidFuncionaisDAO().listarCaractUnidFuncionaisEUnidadeFuncionalCount(objPesquisa, constanteAghCaractUnidFuncionais);
	}
		
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqAndarAlaDescricao(String seqOuAndarAlaDescricao) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqAndarAlaDescricao(seqOuAndarAlaDescricao);
	}
	
	public Integer pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoCount(String seqOuAndarAlaDescricao) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoCount(seqOuAndarAlaDescricao);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMae(String seqOuAndarAlaDescricao) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMae(seqOuAndarAlaDescricao);
	}
	
	public Integer pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMaeCount(String seqOuAndarAlaDescricao) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorSeqAndarAlaDescricaoMaeCount(seqOuAndarAlaDescricao);
	}
	
	@Override
	public List<Short> pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) {
		return getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalTriagemRecepcao(listaUnfSeqTriagemRecepcao, unfSeqMicroComputador);
	}

	public List<RelatorioEtiquetasIdentificacaoVO> pesquisarRelatorioEtiquetasIdentificacao(Short unfSeq, Short unfSeqMae, Integer pacCodigoFonetica,
			Date dataCirurgia) {
		return getAghAtendimentoDAO().pesquisarRelatorioEtiquetasIdentificacao(unfSeq, unfSeqMae, pacCodigoFonetica, dataCirurgia);
	}
	
	public AghAtendimentos buscarAtendimentoPorSeq(Integer seq) {
		return getAghAtendimentoDAO().buscarAtendimentoPorSeq(seq);
	}

	public Boolean verificarUnidadeFuncionalAtendimentos(Short unfSeq) {
		return getAghAtendimentoDAO().verificarUnidadeFuncionalAtendimentos(unfSeq);
	}
	
	@Override
	public List<AghEspecialidades> obterEspecialidadePorProfissionalAmbInt(String param, Integer matricula, Short vinCodigo) {
		return getAghEspecialidadesDAO().obterEspecialidadePorProfissionalAmbInt(param, matricula, vinCodigo);
	}
	
	@Override
	public List<AghEspecialidades> obterEspecialidadeProfCirurgiaoPorServidor(Integer matricula, Short vinCodigo) {
		return getAghEspecialidadesDAO().obterEspecialidadeProfCirurgiaoPorServidor(matricula, vinCodigo);
	}
	
	@Override
	public String obterEspecialidadeConcatenadasProfCirurgiaoPorServidor(Integer serMatricula, Short serVinCodigo) {
		List<AghEspecialidades> listaEspecialidades = this.obterEspecialidadeProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
		String especialidade = null;
		for (AghEspecialidades esp : listaEspecialidades) {
			if (especialidade == null) {
				especialidade = esp.getSigla();
			} else {
				especialidade = especialidade.concat("/").concat(esp.getSigla());
			}
		}
		return especialidade;
	}
	
	/**
	 * Retorna uma Map contendo a PK de RapServidor como chave e a Sigla das especialidades concatenadas.
	 * 
	 */
	@Override
	public Map<String, String> obterMapaEspecialidadeConcatenadasProfCirurgiaoPorServidor(List<Integer> serMatricula, List<Short> serVinCodigo) {
		return getAghEspecialidadesDAO().obterMapEspecialidadeProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
		/*
		Map<String, String> returnMap = new HashMap<String, String>();
		
		List<AghEspecialidades> listaEspecialidades = getAghEspecialidadesDAO().obterEspecialidadeProfCirurgiaoPorServidor(serMatricula, serVinCodigo);
		for (AghEspecialidades especialidade : listaEspecialidades) {
			especialidade.getp
		}
		
		String especialidade = null;
		for (AghEspecialidades esp : listaEspecialidades) {
			if (especialidade == null) {
				especialidade = esp.getSigla();
			} else {
				especialidade = especialidade.concat("/").concat(esp.getSigla());
			}
		}
		
		return returnMap;*/
	}

	
	@Override
	public Integer obterAtendimentoPorConNumero(Integer conNumero) {
		return getAghAtendimentoDAO().obterAtendimentoPorConNumero(conNumero);
	}
		
	@Override
	public Long obterEspecialidadePorProfissionalAmbIntCount(String param, Integer matricula, Short vinCodigo) {
		return getAghEspecialidadesDAO().obterEspecialidadePorProfissionalAmbIntCount(param, matricula, vinCodigo);
	}
	
	@Override
	public List<AghEquipes> pesquisarEquipeRespLaudoAih(Long seq, RapServidores servidorRespInternacao) {
		return getAghEquipesDAO().pesquisarEquipeRespLaudoAih(seq, servidorRespInternacao);
	}
	

	@Override
	public List<AghEspecialidades> pesquisarEspecialidadeLaudoAih(
			Object pesquisa, Integer conNumero, Integer idadePaciente) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadeLaudoAih(pesquisa, conNumero, idadePaciente);
	}

	@Override
	public AghCid obterCidPermiteCidSecundario(Integer cidSeq) {
		return this.getAghCidsDAO().obterCidPermiteCidSecundario(cidSeq);
	}

	@Override
	public List<AghCid> listarCidSecundarioPorIdadeSexoProcedimento(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade,
			String cidCodigo, Short paramTabelaFaturPadrao, Integer caracteristica) {
		return this.getAghCidsDAO().listarCidSecundarioPorIdadeSexoProcedimento(strPesquisa, sexo, idade, cidCodigo, paramTabelaFaturPadrao, caracteristica);
	}

	@Override
	public Long listarCidSecundarioPorIdadeSexoProcedimentoCount(Object strPesquisa, DominioSexoDeterminante sexo, Integer idade,
			String cidCodigo, Short paramTabelaFaturPadrao, Integer caracteristica) {
		return this.getAghCidsDAO().listarCidSecundarioPorIdadeSexoProcedimentoCount(strPesquisa, sexo, idade, cidCodigo, paramTabelaFaturPadrao, caracteristica);
	}

	@Override
	public List<AghCid> pesquisarCidPorCodDescricaoPorSexo(String param, DominioSexo sexo){
		return getAghCidsDAO().pesquisarCidPorCodDescricaoPorSexo(param, sexo);
	}
	
	@Override
	public Long pesquisarCidPorCodDescricaoPorSexoCount(String param, DominioSexo sexo){
		return getAghCidsDAO().pesquisarCidPorCodDescricaoPorSexoCount(param, sexo);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoAtual (AipPacientes paciente) {
		return getAghAtendimentoDAO().obterAtendimentoAtual(paciente);
	}
	
	/**
	 * Obtem a classe CRUD relativa ao pojo AghEquipe
	 * 
	 * @return
	 */
	private EquipeCRUD getEquipeCRUD() {
		return equipeCRUD;
	}

	@Override
	public List<AghProfEspecialidades> listarProfEspecialidadesPorServidor(RapServidores usuarioResponsavel) {
		return getAghProfEspecialidadesDAO().listarProfEspecialidadesPorServidor(usuarioResponsavel);
	}
	
	@Override
	public Boolean isHCPA() {
		return this.getAghuConfigRN().isHCPA();
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}		
	
	@Override
	public List<AghEspecialidades> pesquisarEspecPorNomeSiglaListaSeq(List<Short> listEspId, String parametro){
		return getAghEspecialidadesDAO().pesquisarEspPorNomeOuSiglaListaSeq(listEspId, parametro);
	}
			
	@Override
	public Long pesquisarEspecPorNomeSiglaListaSeqCount(List<Short> listEspId, String parametro){
		return getAghEspecialidadesDAO().pesquisarEspPorNomeOuSiglaListaSeqCount(listEspId, parametro);
	}		

	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqs(listSeqs);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqs(listSeqs, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarEspecialidadesAtivasPorSeqsCount(List<Short> listSeqs) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqsCount(listSeqs);
	}

	@Override
	@Deprecated
	/**
	 * Utilizar pesquisarEspecialidadePorNomeOuSiglaAtivo, pois este método não pesquisa primeiramente por sigla e depois por descrição. Ele faz um "or"
	 * fazendo com que o padrão AGHU não seja respeitado. Se for pesquisado "CAR" deverá trazer somente Cardiologia, e não CARDIO.
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults){
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(param, maxResults);
	}
	
	@Override
	@Deprecated
	/**
	 * Utilizar pesquisarEspecialidadePorNomeOuSiglaAtivoCount, pois este método não pesquisa primeiramente por sigla e depois por descrição. Ele faz um "or"
	 * fazendo com que o padrão AGHU não seja respeitado. Se for pesquisado "CAR" deverá trazer somente Cardiologia, e não CARDIO.
	 */
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(param);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadePorNomeOuSiglaAtivo(String param) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadePorNomeOuSiglaAtivo(param);
	}
	@Override
	public Long pesquisarEspecialidadePorNomeOuSiglaAtivoCount(String param) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadePorNomeOuSiglaAtivoCount(param);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(String param, List<Short> listSeqs) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(param, listSeqs);
	}
	
	@Override
	public Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(String param, List<Short> listSeqs) {
		return getAghEspecialidadesDAO().pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(param, listSeqs);
	}
	
	@Override
	public List<AghCid> pesquisarCidPorCodigoDescricao(String param){
		return getAghCidsDAO().pesquisarCidPorCodDescricao(param);
	}
	
	@Override
	public List<AghCid> pesquisarCidPorSeq(List<Integer> listSeq){
		return getAghCidsDAO().pesquisarCidPorSeq(listSeq);
	}
	
	@Override
	public Integer obterDadosGestantePorGestacaoPaciente(Short gsoSeqp, Integer gsoPacCodigo) {
		return getAghAtendimentoDAO().obterDadosGestantePorGestacaoPaciente(gsoSeqp, gsoPacCodigo);
	}
	
	@Override
	public AghAtendimentos obterDadosGestantePorConsulta(Integer nroConsulta) {
		return getAghAtendimentoDAO().obterDadosGestantePorConsulta(nroConsulta);
	};
	
	@Override
	public Integer obterUltimaConsultaDaPaciente(Integer pacCodigo, List<ConstanteAghCaractUnidFuncionais> caracts) {
		return getAghAtendimentoDAO().obterUltimaConsultaDaPaciente(pacCodigo, caracts);
	};
	
	@Override
	public Long pesquisarCidPorCodigoDescricaoCount(String param) {
		return getAghCidsDAO().pesquisarCidPorCodDescricaoCount(param);
	}
	
	@Override
	public String pesquisarNomeEspecialidadePorSeq(Short seq) {
		return getAghEspecialidadesDAO().pesquisarNomeEspecialidadePorSeq(seq);
	}
	
	@Override
	public List<AghEquipes> pesquisarEquipesAtivasDoCO() {
		return getAghEquipesDAO().pesquisarEquipesAtivasDoCO();
	}
	
	@Override
	public List<AghEquipes> pesquisarEquipesPorMatriculaVinculo(Integer matricula, Short vinCodigo) {
		return getAghEquipesDAO().pesquisarEquipesPorMatriculaVinculo(matricula, vinCodigo);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(String parametro, int firstResult, int maxResults, String orderProperty, boolean asc) {
		return getAghUnidadesFuncionaisDAO().pesquisarAtivasPorSeqOuDescricao(parametro, firstResult, maxResults, orderProperty, asc);
	}
	
	@Override
	public Long pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricaoCount(String parametro) {
		return getAghUnidadesFuncionaisDAO().pesquisarAtivasPorSeqOuDescricaoCount(parametro);
	}
	
	@Override
	public List<AghCid> pesquisarCidAtivosPorSeq(List<Integer> listSeq) {
		return getAghCidsDAO().pesquisarCidAtivosPorSeq(listSeq);
	}
	
	@Override
	public DadosPacientesEmAtendimentoVO obterDadosPacientesEmAtendimento(Integer conNumero) {
		return this.getAghAtendimentoDAO().obterDadosPacientesEmAtendimento(conNumero);
	}
	
	@Override
	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(
			Integer atdSeq, 
			List<DominioOrigemAtendimento> origensInternacao,
			List<DominioOrigemAtendimento> origensAmbulatorio, 
			Integer prontuario,	Integer qtdHorasLimiteAtendimento) {
		return getAghAtendimentoDAO().pesquisarAtendimentoParaPrescricaoMedica(
				atdSeq, origensInternacao, origensAmbulatorio, prontuario,
				qtdHorasLimiteAtendimento);
	}

	@Override
	public List<AghAtendimentos> pesquisarAtendimentosInternacaoNaoUrgencia(final Integer seqInternacao) {
		return getAghAtendimentoDAO().pesquisarAtendimentosInternacaoNaoUrgencia(seqInternacao);
	}

	@Override
	public Collection<PacientesEmAtendimentoVO> listarPacientesEmAtendimentoUnion1(
			Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) {
		return getAghAtendimentoDAO().listarPacientesEmAtendimentoUnion1(pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);
	}

	@Override
	public Collection<PacientesEmAtendimentoVO> listarPacientesEmAtendimentoUnion2(
			Date dataInicio, Date dataFim, Integer pacCodigo, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) {
		return getAghAtendimentoDAO().listarPacientesEmAtendimentoUnion2(dataInicio, dataFim, pacCodigo, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);
	}

	@Override
	public String pesquisarNroRegConselho(Integer matriculaResp,
			Short vinCodigoResp) {
		return getAghProfEspecialidadesDAO().pesquisarNroRegConselho(matriculaResp, vinCodigoResp);
	}
	
	@Override
	public AghUnidadesFuncionais buscarUnidadeInternacaoAtiva(Short unfSeqAgendada) {
		return this.getAghUnidadesFuncionaisDAO().buscarUnidadeInternacaoAtiva(unfSeqAgendada);
	}

	@Override
	public List<AghAtendimentos> buscarAtendimentos(Integer pacCodigo, Date dataPrevisao) {
		return this.getAghAtendimentoDAO().buscarAtendimentos(pacCodigo, dataPrevisao);
	}

	@Override
	public List<ContagemQuimioterapiaVO> buscarCuidadosPrescricaoQuimioterapia(Date dataInicio, Date dataFim) {
		return this.getAghAtendimentoDAO().buscarCuidadosPrescricaoQuimioterapia(dataInicio, dataFim);
	}

	@Override
	public List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosCompetencia(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, SigProcessamentoCusto competenciaSelecionada, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis,Boolean pacienteComAlta) {
		return this.getAghAtendimentoDAO().obterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPaciente(firstResult,maxResult, orderProperty, asc, competenciaSelecionada, listaCID, listaCentroCusto, listaEspecialidades, responsaveis, pacienteComAlta);
	}

	@Override
	public Long obterAghAtendimentosPorFiltrosCompetenciaCount(SigProcessamentoCusto competenciaSelecionada, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis,Boolean pacienteComAlta) {
		return this.getAghAtendimentoON().obterAghAtendimentosPorFiltrosCompetenciaTelaVisualizarCustoPacienteCount(competenciaSelecionada, listaCID, listaCentroCusto, listaEspecialidades, responsaveis, pacienteComAlta);
	}

	@Override
	public List<VAghUnidFuncionalVO> listarUnidadesFuncionaisMae(String seqOuAndarAlaDescricao){
		return getVAghUnidFuncionalDAO().listarUnidadesFuncionaisMae(seqOuAndarAlaDescricao);
	}

	@Override
	public Long listarEspecialidadesSolicitacaoProntuarioCount(Object paramPesquisa) {
		return this.getEspecialidadeON().listarEspecialidadesSolicitacaoProntuarioCount(paramPesquisa, false, true);
	}
	
	@Override
	public Long pesquisarEquipeAtivaCount(Object paramPesquisa) {
		return getAghEquipesDAO().pesquisarEquipeAtivaCount((String)paramPesquisa);
	}
	
	@Override
	public List<AghEquipes> pesquisarEquipeAtivaCO(String parametro) {
		return getAghEquipesDAO().pesquisarEquipeAtivaCO(parametro);
	}
	
	@Override
	public Long pesquisarEquipeAtivaCOCount(String parametro) {
		return getAghEquipesDAO().pesquisarEquipeAtivaCOCount(parametro);
	}
	
	@Override
	public List<AghEspecialidades> listarEspecialidadesAtivasPorNomeOuSigla(String parametro) {
		return getAghEspecialidadesDAO().listarEspecialidadesAtivasPorNomeOuSigla(parametro);
	}

	public Long listarEspecialidadesAtivasPorNomeOuSiglaCount(String parametro) {
		return getAghEspecialidadesDAO().listarEspecialidadesAtivasPorNomeOuSiglaCount(parametro);
	}
	
	@Override
	public List<AghAtendimentosVO> obterAghAtendimentosPorFiltrosPaciente(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, AipPacientes paciente, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		return this.getAghAtendimentoON().obterAghAtendimentosPorFiltrosPaciente(firstResult,maxResult, orderProperty, asc, paciente, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}
	
	@Override
	public Long obterAghAtendimentosPorFiltrosPacienteCount(AipPacientes paciente, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		return this.getAghAtendimentoON().obterAghAtendimentosPorFiltrosPacienteCount(paciente, listaCID, listaCentroCusto, listaEspecialidades, responsaveis);
	}
	
	@Override
	public List<AghProfissionaisEquipe> pesquisarServidoresPorEquipe(Integer eqpSeq) {
		return getAghProfissionaisEquipeDAO().pesquisarServidoresPorEquipe(eqpSeq);
	}

	public AghAtendimentoON getAghAtendimentoON() {
		return aghAtendimentoON;
	}
	
	
	/**
	 * #34722 - Consulta utilizada para verificar se determinada unidade funcional possui determinada característica associada
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	@Override
	public Boolean existeCaractUnidFuncionaisPorSeqCaracteristica(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
	return getAghCaractUnidFuncionaisDAO().existeCaractUnidFuncionaisPorSeqCaracteristica(unfSeq, caracteristica);
	}
	
	@Override
	public List<AghUnidadesFuncionais> pesquisarUnidadesAtivas(final String strPesquisa, final boolean semEtiologia, final String tipo) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesAtivas(strPesquisa, semEtiologia, tipo);
	}
	@Override
	public Long pesquisarUnidadesAtivasCount(final String strPesquisa, final boolean semEtiologia, final String tipo) {
		return getAghUnidadesFuncionaisDAO().pesquisarUnidadesAtivasCount(strPesquisa, semEtiologia, tipo);
	}

	protected AghProfissionaisEquipeDAO getAghProfissionaisEquipeDAO() {
		return aghProfissionaisEquipeDAO;
	}
	
	
	@Override
	public Long pesquisarCaracteristicasEspecialidadesCount(AghEspecialidades especialidade) {
		return this.getAghCaractEspecialidadesDAO().pesquisarCaracteristicasEspecialidadesCount(especialidade);
	}

	@Override
	public List<AghCaractEspecialidades> pesquisarCaracteristicaEspecialidade(Integer firstResult, Integer maxResult, String orderProperty, AghEspecialidades especialidade, boolean ordenacao) {
		return this.getAghCaractEspecialidadesDAO().pesquisarCaracteristicaEspecialidade(firstResult, maxResult, orderProperty, especialidade, ordenacao);
	}

	@Override
	@Secure("#{s:hasPermission('manterCaracteristicasEspecialidades', 'alterar')}")
	public void removerCaracteristicaEspecialidade(AghCaractEspecialidades acAghCaractEspecialidade) {
		this.getAghCaractEspecialidadesDAO().remover(acAghCaractEspecialidade);
		this.getAghCaractEspecialidadesDAO().flush();
	}

	@Override
	public void desassociarCaracteristicaEspecialidade(AghCaractEspecialidadesId id) {
		getAghCaractEspecialidadesDAO().removerPorId(id);
		getAghCaractEspecialidadesDAO().flush();
	}
	
	@Override
	@Secure("#{s:hasPermission('manterCaracteristicasEspecialidades', 'alterar')}")
	public void persistirAghCaractEspecialidade(AghCaractEspecialidades aghCaractEspecialidades) throws BaseException {
		this.getAghCaractEspecialidadesDAO().persistir(aghCaractEspecialidades);
		this.getAghCaractEspecialidadesDAO().flush();
	}

	@Override
	@Secure("#{s:hasPermission('manterCaracteristicasEspecialidades', 'alterar')}")
	public void atualizarAghCaractEspecialidade(AghCaractEspecialidades aghCaractEspecialidades, AghCaractEspecialidades aghCaractEspecialidadeOld) throws BaseException {
		getAghCaractEspecialidadeRN().atualizarAghCaractEspecialidade(aghCaractEspecialidades, aghCaractEspecialidadeOld);
	}

	@Override
	public AghCaractEspecialidades obterCaracteristicaEspecialidadePorId(AghCaractEspecialidadesId id) {
		return this.getAghCaractEspecialidadesDAO().obterPorChavePrimaria(id);
	}
	
	protected AghCaractEspecialidadeRN getAghCaractEspecialidadeRN() {
		return new AghCaractEspecialidadeRN();
	}
	
	@Override
	public AghCaractEspecialidades obterCaracteristicaEspecialidade(AghCaractEspecialidades aghCaractEspecialidades) {
		return this.getAghCaractEspecialidadesDAO().obterPorChavePrimaria(aghCaractEspecialidades.getId());
	}

	@Override
	public AghCaractEspecialidades obterCaracteristicaEspecialidadeOld(AghCaractEspecialidades aghCaractEspecialidades) {
		return this.getAghCaractEspecialidadesDAO().obterOriginal(aghCaractEspecialidades);
	}

	@Override
	public List<Short> pesquisarUnidFuncExecutora(){
		return this.getAghCaractUnidFuncionaisDAO().pesquisarUnidFuncExecutora();
	}
	
	@Override
	public AghAtendimentos buscarAtendimentoPorConNumero(Integer conNumero){
		return getAghAtendimentoDAO().buscarAtendimentoPorConNumero(conNumero);
	}
	
	/**
	 * # 39006 - Serviço que obtem AghAtendimentos
	 * @param seq
	 * @return
	 */
	@Override
	public AghAtendimentos obterAghAtendimentosPorSeq(Integer seq){
		return getAghAtendimentoDAO().obterAghAtendimentosPorSeq(seq);
	}	

	@Override
	public List<AghEspecialidades> obterListaEspSiglaOuNomeOrdSigla(String parametro){
		return getAghEspecialidadesDAO().pesquisarPorSiglaOuNomeOrdSigla(parametro);
	}
	
	@Override
	public AghAtendimentos obterAtendimentoPorPacienteDataInicio(Integer pacCodigo, Date dthrInicio) {
		return getAghAtendimentoDAO().obterAtendimentoPorPacienteDataInicio(pacCodigo, dthrInicio);
	}
	
	@Override
	public void atualizarAtendimentoDthrNascimento(Integer pacCodigo, Date dthrInicio, Date dthrNascimento) {
		getAghAtendimentoON().atualizarAtendimentoDthrNascimento(pacCodigo, dthrInicio, dthrNascimento);
	}
	
	@Override
	public AghEspecialidades buscarEspecialidadePorConNumero(Integer conNumero) {
		return getAghEspecialidadesDAO().buscarEspecialidadePorConNumero(conNumero);
	}
	
	@Override
	@Secure("#{s:hasPermission('manterEquipesPorProfissional','alterar')}")
	public void persistirAghProfissionalEquipe(AghProfissionaisEquipe equipePorProfissional) {
		this.getAghProfissionaisEquipeDAO().persistir(equipePorProfissional);
	}

	@Override
	@Secure("#{s:hasPermission('manterEquipesPorProfissional','alterar')}")
	public void removerAghProfissionaisEquipe(AghProfissionaisEquipe equipePorProfissional) {
		this.getAghProfissionaisEquipeDAO().remover(equipePorProfissional);
	}
	
	@Override
	public AghProfissionaisEquipe obterAghProfissionaisEquipePorChavePrimaria(AghProfissionaisEquipeId equipePorProfissionalid) {
		return this.getAghProfissionaisEquipeDAO().obterPorChavePrimaria(equipePorProfissionalid);
	}

	@Override
	@Secure("#{s:hasPermission('manterEquipesPorProfissional','pesquisar')}")
	public List<AghProfissionaisEquipe> pesquisarEquipesPorProfissionalPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores servidor) {
		return getAghProfissionaisEquipeDAO().pesquisarEquipesPorProfissionalPaginado(firstResult, maxResult, orderProperty, asc, servidor);
	}

	@Override
	@Secure("#{s:hasPermission('manterEquipesPorProfissional','pesquisar')}")
	public Long countPesquisarEquipesPorProfissionalPaginado(RapServidores servidor) {
		return getAghProfissionaisEquipeDAO().countPesquisarEquipesPorProfissionalPaginado(servidor);
	}

	@Override
	public String obterDescricaoEquipe(Integer matricula, Short vinCodigo) {
		return this.getAghEquipesDAO().obterDescricaoEquipe(matricula, vinCodigo);
	}	
	
	@Override
	public List<AghEquipes> pesquisarListaEquipes(String objPesquisa) {
		return getAghEquipesDAO().pesquisarEquipeAtiva(objPesquisa);
	}

	@Override
	public Long pesquisarListaEquipesCount(String objPesquisa) {
		return getAghEquipesDAO().pesquisarEquipeAtivaCount(objPesquisa);
	}
	
	@Override
	public Integer countNumLinhasTabela(String nomeTabela) {
		return getAghTabelasSistemaDAO().countNumLinhasTabela(nomeTabela);
	}

	@Override
	public boolean verificarTabelaExistente(String nomeTabela) {
		return getAghTabelasSistemaDAO().verificarTabelaExistente(nomeTabela);
	}
	
	@Override
	public Boolean existePacienteInternadoListarPacientesCCIH(Integer codigoPaciente) {
		return aghAtendimentoDAO.existePacienteInternadoListarPacientesCCIH(codigoPaciente);
	}

	@Override
	public List<AghUnidadesFuncionais> obterUnidadesFuncionaisSB(Object param) {
		return aghUnidadesFuncionaisDAO.obterUnidadesFuncionaisSB(param);
	}

	@Override
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaOrderByNomeReduzido(String parametro) {
		return aghEspecialidadesDAO.pesquisarPorNomeOuSiglaOrderByNomeReduzido(parametro);
	}

	@Override
	public Long pesquisarPorNomeOuSiglaOrderByNomeReduzidoCount(String param) {
		return aghEspecialidadesDAO.pesquisarPorNomeOuSiglaOrderByNomeReduzidoCount(param);
	}

	@Override
	public Long obterUnidadesFuncionaisSBCount(String param) {
		return aghUnidadesFuncionaisDAO.obterUnidadesFuncionaisSBCount(param);
	}	
	@Override
	public AghAtendimentos obterAghAtendimentoPorChavePrimaria(Integer chavePrimaria, Enum[] aliasInner, Enum[] aliasLeft) {
		return getAghAtendimentoDAO().obterPorChavePrimaria(chavePrimaria, aliasInner, aliasLeft);
	}
	
	public List<AghEspecialidadeVO> pesquisarAghEspecialidadePorServidor(RapServidores servidor) {
		return aghEspecialidadesDAO.pesquisarAghEspecialidadePorServidor(servidor);
	}
	
	public List<AghEspecialidadeVO> pesquisarEspecialidadesConsultoria(String pesquisa, Boolean indPesquisaConsultoriaAmbulatorio, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return aghEspecialidadesDAO.pesquisarEspecialidadesConsultoria(pesquisa, indPesquisaConsultoriaAmbulatorio, firstResult, maxResult, orderProperty, asc);
	}
	public Long pesquisarEspecialidadesConsultoriaCount(String pesquisa, Boolean indPesquisaConsultoriaAmbulatorio) {
		return aghEspecialidadesDAO.pesquisarEspecialidadesConsultoriaCount(pesquisa,indPesquisaConsultoriaAmbulatorio);
	}
	
	public List<EquipeVO> pesquisarEquipesConsultoriaAmbulatorial(final String pesquisa, Short espSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return aghEquipesDAO.pesquisarEquipesConsultoriaAmbulatorial(pesquisa, espSeq, firstResult, maxResult, orderProperty, asc);
	}
	public Long pesquisarEquipesConsultoriaAmbulatorialCount(final String pesquisa, Short espSeq) {
		return aghEquipesDAO.pesquisarEquipesConsultoriaAmbulatorialCount(pesquisa, espSeq);
	}
	
	public void persistirProfEspecialidades(AghProfEspecialidades profEspecialidades, RapServidores servidorLogado, Short espSeq) throws ApplicationBusinessException {
		aghProfEspecialidadesRN.persistirAghProfEspecialidades(profEspecialidades, servidorLogado, espSeq);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarSelecaoEspecialidades(String parametro) {
		return aghEspecialidadesDAO.pesquisarSelecaoEspecialidades(parametro);
	}

	@Override
	public AghAtendimentos obterAtendimentoPorPacienteDataInicioOrigem(
			Integer pacCodigo, Date dthrInicio, DominioOrigemAtendimento[] dominioOrigemAtendimento) {
		return aghAtendimentoDAO.obterAtendimentoPorPacienteDataInicioOrigem(pacCodigo, dthrInicio, dominioOrigemAtendimento);
	}

	@Override
	public Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigo) {
		return aghAtendimentoDAO.obterUltimoAtdSeqRecemNascidoPorPacCodigo(pacCodigo);
	}

	@Override
	public List<AtendimentoNascimentoVO> pesquisarAtendimentosPorPacienteGestacaoEDataDeInternacao(
			Integer internacaoSeq, Integer gsoPacCodigo, Short gsoSeqp,
			Date dataInternacaoAdministrativa, Date dtAltaAdministrativa) {
		
		return this.getAghAtendimentoDAO().pesquisarAtendimentosPorPacienteGestacaoEDataDe(internacaoSeq, gsoPacCodigo, gsoSeqp, dataInternacaoAdministrativa, dtAltaAdministrativa);
	}
	
	@Override
	public Long pesquisarSelecaoEspecialidadesCount(String parametro) {
		return aghEspecialidadesDAO.pesquisarSelecaoEspecialidadesCount(parametro);
	}
	
	@Override
	public List<AtendimentoNascimentoVO> pesquisarAtendimentosPorPacienteGestacao(
			Integer internacaoSeq, Integer gsoPacCodigo, Short gsoSeqp) {
		
		return this.getAghAtendimentoDAO().pesquisarAtendimentosPorPacienteGestacao(internacaoSeq, gsoPacCodigo, gsoSeqp);
	
	}

	@Override
	public List<CidVO> obterCidPorDominioVivoMorto(DominioVivoMorto vivo, DominioVivoMorto morto) {
		return this.getAghCidsDAO().obterCidPorDominioVivoMorto(vivo, morto);
	}
	
	@Override
	public List<AghAtendimentosPacienteCnsVO> buscarAtendimentosFetchAipPacientesDadosCns(Integer adtSeq){
		return this.getAghAtendimentoDAO().buscarAtendimentosFetchAipPacientesDadosCns(adtSeq);
	}

	@Override
	public AghAtendimentos pesquisarAtendimentosComOrigemUrgencia(Integer pacCodigo, Short gsoSeqp) {
		return this.getAghAtendimentoDAO().pesquisarAtendimentosComOrigemUrgencia(pacCodigo, gsoSeqp);
	}

	@Override
	public AghAtendimentos pesquisarAtendimentosComOrigemAmbulatorial(Integer pacCodigo, Short gsoSeqp) {
		return this.getAghAtendimentoDAO().pesquisarAtendimentosComOrigemAmbulatorial(pacCodigo, gsoSeqp);
	}


	@Override
	public Long obterUnidadesFuncionaisSBCount(Object param) {
		return aghUnidadesFuncionaisDAO.obterUnidadesFuncionaisSBCount(param);
	}

	@Override
	public List<AghCid> pesquisarCidPorCodigoDescricaoSGB(String param){
		return getAghCidsDAO().pesquisarCidPorCodDescricaoSGB(param);
	}

	@Override
	public List<AghImpressoraPadraoUnids> listarAghImpressoraPadraoUnids(
			Short seq, TipoDocumentoImpressao tipoDocumentoImpressao) {
		return getAghImpressoraPadraoUnidsDAO().listarAghImpressoraPadraoUnids(seq, tipoDocumentoImpressao);
	}

	@Override
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterEquipePorSeq(Integer seq) {
		return aghEquipesDAO.obterEquipePorSeq(seq);
	}
	@Override
	public List<AghUnidadesFuncionais> obterUnidadesFuncionaisComLeitosEPodemSolicitarExames(
			Object parametro){
		return getAghUnidadesFuncionaisDAO().obterUnidadesFuncionaisComLeitosEPodemSolicitarExames(parametro);
	}

	@Override
	public Long obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(
			Object parametro){
		return getAghUnidadesFuncionaisDAO().obterUnidadesFuncionaisComLeitosEPodemSolicitarExamesCount(parametro);
	}
	
	@Override
	public List<AghCid> obterCidPorNomeCodigoAtivaPaginado(String param, SigProcessamentoCusto competencia) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaPaginado(param, competencia);
	}
	
	@Override
	public Long obterCidPorNomeCodigoAtivaCount(String param, SigProcessamentoCusto competencia) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaCount(param, competencia);
	}
	
	@Override
	public List<AghAtendimentos> pesquisarPacientesInternados(AipPacientes aipPaciente, AinQuartos quarto, AinLeitos leito, AghUnidadesFuncionais unidadeFuncional){
		return this.getAghAtendimentoDAO().pesquisarPacientesInternados(aipPaciente, quarto, leito, unidadeFuncional);
	}
	@Override
	public List<AghEspecialidadeVO> pesquisarEspecialidadesPorSiglaNomeCodigo(String pesquisa)throws ApplicationBusinessException{
		return aghEspecialidadesDAO.pesquisarEspecialidadesPorSiglaNomeCodigo(pesquisa);		
	}
	@Override
	public Long pesquisarEspecialidadesPorSiglaNomeCodigoCount(String pesquisa)throws ApplicationBusinessException{
		return aghEspecialidadesDAO.pesquisarEspecialidadesPorSiglaNomeCodigoCount(pesquisa);			
	}
	@Override
	public List<AghEquipes> pesquisarEquipes(String pesquisa){
		return aghEquipesDAO.pesquisarEquipes(pesquisa);
	}
	@Override
	public Long pesquisarEquipesCount(String pesquisa){
		return aghEquipesDAO.pesquisarEquipesCount(pesquisa);
	}
	
	//50635 - C1
	@Override	
	public Integer obterAghAtendimentoPorProntuario(Integer prontuario) {
		return this.getAghAtendimentoDAO().obterAghAtendimentoPorProntuario(prontuario);
	}
	
	@Override							 
	public List<AghCaractUnidFuncionais> pesquisarCaracteristicasUnidadeFuncionalPorCaracteristica(
			List<ConstanteAghCaractUnidFuncionais> listaCaracteristicas) {
		return this.getAghCaractUnidFuncionaisDAO().pesquisarCaracteristicasUnidadeFuncionalPorCaracteristica(listaCaracteristicas);
	}
	@Override
	public List<AghEspecialidades> listarEspecialidadesPorServidor(Integer matricula, Short vinCodigo) {
		return this.getAghEspecialidadesDAO().listarEspecialidadesPorServidor(matricula, vinCodigo);
	}

	@Override
	public AghAtendimentos obterUltimoAtendimentoEmAndamentoPorPaciente(Integer pacCodigo) {
		return this.getAghAtendimentoDAO().obterUltimoAtendimentoEmAndamentoPorPaciente(pacCodigo);
	}

	@Override
	public AghAtendimentos obterAghAtendimentosPorChavePrimaria(Integer seq) {
		return getAghAtendimentoDAO().obterPorChavePrimaria(seq);
	}
	
	@Override
	public void gravarPesquisaMenuLog(String nome, String url, RapServidores servidorLogado) {
		getAghPesquisaMenuLogRN().gravarPesquisaMenuLog(nome, url, servidorLogado);
	}
		
	
	@Override
	public AghAtendimentos obterAghAtendimentosAnamneseEvolucao(Integer seqAtendimento){
		return getAghAtendimentoDAO().obterAghAtendimentosAnamneseEvolucao(seqAtendimento);
	}
	
	@Override
	public List<AghEspecialidades> obterEspecialidadePorSiglas(List<String> siglas) {
		return this.getAghEspecialidadesDAO().obterEspecialidadePorSiglas(siglas);
	}
	
	@Override
	public List<Integer> pesquisarIdsArquivosAbortados(final AghSistemas sistema, Integer minutosVencimento) {
		return getAghArquivoProcessamentoDAO().pesquisarIdsArquivosAbortados(sistema, minutosVencimento);
	}

	@Override
	public AghArquivoProcessamento obterArquivoNaoProcessadoVO(AghSistemas sistema, String nome) {
		return getAghArquivoProcessamentoDAO().obterArquivoNaoProcessadoVO(sistema, nome);
	}	
	
	@Override
	public void atualizarAghArquivoProcessamento(Integer seq, Date date, Integer percent, Date fimProcessamento) {
		this.getAghArquivoProcessamentoDAO().atualizarAghArquivoProcessamento(seq, date, percent, fimProcessamento);
		this.getAghArquivoProcessamentoDAO().flush();
	}
	
	// #12158
	@Override
	public List<AghCid> pesquisarListaAghCid(String parametro) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtiva(parametro);
	}
	
	@Override
	public Long pesquisarListaAghCidCount(String parametro) {
		return getAghCidsDAO().pesquisarPorNomeCodigoAtivaCount(parametro);
	}
	
	@Override
	public List<AghEspecialidades> pesquisarListaAghEspecialidade(String parametro) {
		return getAghEspecialidadesDAO().pesquisarAghEspecialidadeAtivasEspSeqNull(parametro);
	}
	
	@Override
	public Long pesquisarListaAghEspecialidadeCount(String parametro) {
		return getAghEspecialidadesDAO().pesquisarAghEspecialidadeAtivasEspSeqNullCount(parametro);
	}

	@Override
	public Short obtemUnfSeqPorAlmoxarifado(Short almSeq) {
		return getAghUnidadesFuncionaisDAO().obtemUnfSeqPorAlmoxarifado(almSeq);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(String strPesquisa, String ordernar,
			boolean apenasAtivos, Object[] caracteristicas){
		return this.getAghUnidadesFuncionaisDAO().pesquisarUnidadeFuncionalPorCodigoDescricaoComFiltroPorCaract(strPesquisa,
				ordernar, apenasAtivos, caracteristicas);
	}
	
}