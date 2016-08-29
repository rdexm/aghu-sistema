package br.gov.mec.aghu.farmacia.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao;
import br.gov.mec.aghu.exames.vo.UnitarizacaoVO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComposicaoItemPreparoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoApresMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoComponenteNptJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoMedicamentoMensagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoUsoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemPreparoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPreparoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTempoEstabilidadesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTempoEstabilidadesJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoApresentacaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoOcorDispensacaoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoUsoMdtoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdmUnfDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.VAfaDescrMdtoDAO;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesJnVO;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesVO;
import br.gov.mec.aghu.farmacia.vo.ComposicaoItemPreparoVO;
import br.gov.mec.aghu.farmacia.vo.DiluentesVO;
import br.gov.mec.aghu.farmacia.vo.HistoricoCadastroMedicamentoVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoDispensadoPorBoxVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoEstornadoMotivoVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoPacienteVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoUnidadeVO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoSinteticoVO;
import br.gov.mec.aghu.farmacia.vo.MedicoResponsavelVO;
import br.gov.mec.aghu.farmacia.vo.PrescricaoUnidadeVO;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaGrupoApresMdtos;
import br.gov.mec.aghu.model.AfaGrupoComponNptJn;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaItemPreparoMdto;
import br.gov.mec.aghu.model.AfaItemPreparoMdtoId;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.model.AfaMedicamentoJn;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoJN;
import br.gov.mec.aghu.model.AfaVinculoDiluentes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.vo.AfaViaAdmUnfVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoPrescricaoMedicaVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.vo.PacientesEmAtendimentoVO;

/**
 * Porta de entrada do módulo de prescrição.
 * 
 * @author gmneto
 * 
 */


@Modulo(ModuloEnum.FARMACIA)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class FarmaciaFacade extends BaseFacade implements IFarmaciaFacade {


	@EJB
	private RelatorioMedicamentosPrescritosPorPacienteON relatorioMedicamentosPrescritosPorPacienteON;
	
	@EJB
	private HistoricoMedicamentoEquivalenteON historicoMedicamentoEquivalenteON;
	
	@EJB
	private AfaDispensacaoMdtosRN afaDispensacaoMdtosRN;
	
	@EJB
	private RelatorioMedicamentoSinteticoON relatorioMedicamentoSinteticoON;
	
	@EJB
	private FarmaciaRN farmaciaRN;
	
	@EJB
	private FarmaciaON farmaciaON;
	
	@EJB
	private CadastroDiluentesON cadastroDiluentesON;
	
	@EJB
	private RelatorioMedicamentoPrescritoPorUnidadeRN relatorioMedicamentoPrescritoPorUnidadeRN;
	
	@EJB
	private RelatorioMedicamentoPrescritoPorUnidadeON relatorioMedicamentoPrescritoPorUnidadeON;
	
	@EJB
	private RelatorioQuantidadePrescricoesDispensacaoON relatorioQuantidadePrescricoesDispensacaoON;
	
	@EJB
	private HistoricoViasAdministracaoON historicoViasAdministracaoON;
	
	@EJB
	private EtiquetasON etiquetasON;
	
	@EJB
	private RelatorioMedicamentoEstornadoPorMotivoON relatorioMedicamentoEstornadoPorMotivoON;
	
	@EJB
	private HistoricoCadastroMedicamentoON historicoCadastroMedicamentoON;
	
	@EJB
	private RelatorioPrescricaoPorUnidadeON relatorioPrescricaoPorUnidadeON;
	
	@EJB
	private RelatorioPrescricaoPorUnidadeRN relatorioPrescricaoPorUnidadeRN;
	
	@EJB
	private RelatorioMedicamentoDispensadoPorBoxON relatorioMedicamentoDispensadoPorBoxON;
	
	@EJB
	private HistoricoSinonimoMedicamentoON historicoSinonimoMedicamentoON;
	
	@EJB
	private HistoricoFormaDosagemON historicoFormaDosagemON;
	
	@EJB
	private FarmaciaEmergiaON farmaciaEmergiaON;
	
	@Inject
	private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;
	
	@Inject
	private AfaGrupoComponenteNptJnDAO afaGrupoComponenteNptJnDAO;
	
	@Inject
	private PesquisaPacientesEmAtendimentoON pesquisaPacientesEmAtendimentoON;
	
	@Inject
	private AfaGrupoUsoMedicamentoDAO afaGrupoUsoMedicamentoDAO;
	
	@Inject
	private AfaViaAdministracaoDAO afaViaAdministracaoDAO;
	
	@Inject
	private AfaViaAdmUnfDAO afaViaAdmUnfDAO;
	
	@Inject
	private AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO;
	
	@Inject
	private AfaTempoEstabilidadesJnDAO afaTempoEstabilidadesJnDAO;
	
	@Inject
	private AfaTipoOcorDispensacaoDAO afaTipoOcorDispensacaoDAO;
	
	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;
	
	@Inject
	private AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO;
	
	@Inject
	private AfaTipoApresentacaoMedicamentoDAO afaTipoApresentacaoMedicamentoDAO;
	
	@Inject
	private AfaViaAdministracaoMedicamentoDAO afaViaAdministracaoMedicamentoDAO;
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	
	@Inject
	private AfaGrupoApresMdtosDAO afaGrupoApresMdtosDAO;
	
	@Inject
	private AfaLocalDispensacaoMdtosJnDAO afaLocalDispensacaoMdtosJnDAO;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Inject
	private AfaTipoUsoMdtoDAO afaTipoUsoMdtoDAO;
	
	@Inject
	private VAfaDescrMdtoDAO vAfaDescrMdtoDAO;
	
	@Inject
	private AfaTempoEstabilidadesDAO afaTempoEstabilidadesDAO;
	
	@Inject
	private AfaGrupoMedicamentoDAO afaGrupoMedicamentoDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Inject
	private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;
	
	@Inject
	private AfaGrupoMedicamentoMensagemDAO afaGrupoMedicamentoMensagemDAO;
	
	@Inject
	private AfaComposicaoItemPreparoDAO afaComposicaoItemPreparoDAO;
	
	@Inject
	private AfaPreparoMdtosDAO afaPreparoMdtosDAO;
	@Inject
	private AfaItemPreparoMdtosDAO afaItemPreparoMdtosDAO;
	
	@Inject
	private AfaComponenteNptJnDAO afaComponenteNptJnDAO; 
	
	@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Inject
	private AfaDispensacaoDiluenteON afaDispensacaoDiluenteON;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7397876968103487752L;


	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#persistirAfaTipoVelocAdministracoes(br.gov.mec.aghu.model.AfaTipoVelocAdministracoes)
	 */
	@Override
	public void persistirAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
			throws BaseException {
		getFarmaciaON().persistirAfaTipoVelocAdministracoes(
				tipoVelocidadeAdministracao);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterViaAdministracao(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public AfaViaAdministracao obterViaAdministracao(String siglaViaAdministracao) {
		return getAfaViaAdministracaoDAO().obterPorChavePrimaria(siglaViaAdministracao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamento(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AfaMedicamento obterMedicamento(Integer codigoMedicamento) {
		return getAfaMedicamentoDAO().obterPorChavePrimaria(codigoMedicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#buscarDosagenPadraoMedicamento(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AfaFormaDosagem buscarDosagenPadraoMedicamento(Integer medMatCodigo) {
		return this.getFarmaciaON().buscarDosagenPadraoMedicamento(medMatCodigo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#buscarAfaViaAdministracaoMedimanetoPorChavePrimaria(br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId)
	 */
	@Override
	@BypassInactiveModule
	public AfaViaAdministracaoMedicamento buscarAfaViaAdministracaoMedimanetoPorChavePrimaria(
			AfaViaAdministracaoMedicamentoId chave) {
		return this.getFarmaciaON().obterAfaViaAdministracaoMedicamento(chave);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obtemListaDiluentes()
	 */
	@Override
	@BypassInactiveModule
	public List<VAfaDescrMdto> obtemListaDiluentes() {
		return this.getFarmaciaON().obtemListaDiluentes();
	}

	@Override
	@BypassInactiveModule
	public List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> pesquisarMedicamentoPorCodigosEmergencia(List<Integer> matCodigos) {
		return farmaciaEmergiaON.pesquisarMedicamentoPorCodigos(matCodigos);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obtemListaDiluentes(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public List<VAfaDescrMdto> obtemListaDiluentes(Integer matCodigo) {
		return this.getFarmaciaON().obtemListaDiluentes(matCodigo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obtemListaTiposVelocidadeAdministracao()
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoVelocAdministracoes> obtemListaTiposVelocidadeAdministracao() {
		return this.getFarmaciaON().obtemListaTiposVelocidadeAdministracao();
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarViasMedicamento(java.lang.String, java.util.List, java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa,
			List<Integer> medMatCodigo, Short seqUnidadeFuncional) {
		return this.getFarmaciaON().listarViasMedicamento(strPesquisa, medMatCodigo,
				seqUnidadeFuncional);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarViasMedicamentoCount(java.lang.String, java.util.List, java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public Long listarViasMedicamentoCount(String strPesquisa, List<Integer> medMatCodigo,
			Short seqUnidadeFuncional) {
		return this.getFarmaciaON().listarViasMedicamentoCount(strPesquisa, medMatCodigo,
				seqUnidadeFuncional);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterInfromacoesFarmacologicas(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public String obterInfromacoesFarmacologicas(AfaMedicamento medicamento) {
		return this.getFarmaciaON().obterInformacoesFarmacologicas(medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarTodasAsVias(java.lang.String, java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> listarTodasAsVias(String strPesquisa, Short unfSeq) {
		return this.getFarmaciaON().listarTodasAsVias(strPesquisa, unfSeq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarTodasAsViasCount(java.lang.String, java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public Long listarTodasAsViasCount(String strPesquisa, Short unfSeq) {
		return this.getFarmaciaON().listarTodasAsViasCount(strPesquisa, unfSeq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#verificarViaAssociadaAoMedicamento(java.lang.Integer, java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public Boolean verificarViaAssociadaAoMedicamento(Integer medMatCodigo, String sigla) {
		return this.getFarmaciaON().verificarViaAssociadaAoMedicamento(medMatCodigo, sigla);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(java.lang.Integer, java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public Boolean verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(
			Integer medMatCodigo, String sigla) {
		return this.getFarmaciaON()
				.verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(
						medMatCodigo, sigla);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obtemTipoUsoMedicamentoComDuracSol(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public String obtemTipoUsoMedicamentoComDuracSol(Integer matCodigo) {
		return getAfaMedicamentoDAO().obtemTipoUsoMedicamentoComDuracSol(matCodigo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#isTipoVelocidadeAtiva(java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public boolean isTipoVelocidadeAtiva(Short seq) throws ApplicationBusinessException {
		return getFarmaciaON().isTipoVelocidadeAtiva(seq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosVO(java.lang.Object, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoVO> obterMedicamentosVO(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes,
			Boolean somenteMdtoDeUsoAmbulatorial, Boolean listaMedicamentosAux) {
		return this.getAfaMedicamentoDAO().obterMedicamentosVO(strPesquisa,
				listaMedicamentos, situacoes, somenteMdtoDeUsoAmbulatorial, listaMedicamentosAux);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosModeloBasicoVO(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoVO> obterMedicamentosModeloBasicoVO(Object strPesquisa) {
		return this.getFarmaciaON().obterMedicamentosModeloBasicoVO(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosVOCount(java.lang.Object, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public Long obterMedicamentosVOCount(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes,
			Boolean somenteMdtoDeUsoAmbulatorial, Boolean listaMedicamentosAux) {
		return this.getAfaMedicamentoDAO().obterMedicamentosVOCount(
				strPesquisa, listaMedicamentos, situacoes,
				somenteMdtoDeUsoAmbulatorial, listaMedicamentosAux);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosModeloBasicoVOCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long obterMedicamentosModeloBasicoVOCount(Object strPesquisa) {
		return this.getFarmaciaON().obterMedicamentosModeloBasicoVOCount(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentos(java.lang.Object, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> obterMedicamentos(Object strPesquisa, Boolean listaMedicamentos) {
		return this.getFarmaciaON().obterMedicamentos(strPesquisa, listaMedicamentos);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosCount(java.lang.Object, java.lang.Boolean)
	 */
	@Override
	public Integer obterMedicamentosCount(Object strPesquisa, Boolean listaMedicamentos) {
		return this.getFarmaciaON().obterMedicamentosCount(strPesquisa, listaMedicamentos);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obtemSituacaoFormaDosagem(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public DominioSituacao obtemSituacaoFormaDosagem(Integer seq, Integer medMatCodigo) {
		return getAfaFormaDosagensDAO().obtemSituacaoFormaDosagem(seq, medMatCodigo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listaTipoUsoMedicamentoPorMedicamentoEGupSeqCount(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public Long listaTipoUsoMedicamentoPorMedicamentoEGupSeqCount(Integer codigoMedicamento,
			Integer gupSeq) {
		return getAfaTipoUsoMdtoDAO().listaTipoUsoMedicamentoPorMedicamentoEGupSeqCount(
				codigoMedicamento, gupSeq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getListaTodasAsVias(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> getListaTodasAsVias(String strPesquisa) {
		return getAfaViaAdministracaoDAO().listarTodasAsVias(strPesquisa);
	}
	
	@Override
	public List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> pesquisarMedicamentoAtivoPorCodigoOuDescricao(String parametro) {
		return farmaciaEmergiaON.pesquisarMedicamentoAtivoPorCodigoOuDescricao(parametro);
	}
	
	@Override
	public br.gov.mec.aghu.farmacia.vo.MedicamentoVO buscarMedicamentoPorCodigo(Integer matCodigo) {
		return farmaciaEmergiaON.buscarMedicamentoPorCodigo(matCodigo);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getListaViasAdmNaoUtilizadas(java.lang.String, java.util.Set)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> getListaViasAdmNaoUtilizadas(String strPesquisa, Set<AfaViaAdministracaoMedicamento> listaViasAdm) {
		return getAfaViaAdministracaoDAO().listarViasAdmNaoUtilizadas(strPesquisa, listaViasAdm);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getListaViasMedicamento(java.lang.String, java.util.List)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> getListaViasMedicamento(
			String strPesquisa, List<Integer> listaDeIds) {
		return getAfaViaAdministracaoDAO().listarViasMedicamento(strPesquisa,
				listaDeIds);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getListaTodasAsViasCount(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public Long getListaTodasAsViasCount(String strPesquisa) {
		return this.getAfaViaAdministracaoDAO().listarTodasAsViasCount(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getListaViasMedicamentoCount(java.lang.String, java.util.List)
	 */
	@Override
	public Long getListaViasMedicamentoCount(String strPesquisa, List<Integer> listaDeIds) {
		return this.getAfaViaAdministracaoDAO().listarViasMedicamentoCount(strPesquisa, listaDeIds);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosReceitaVO(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<VAfaDescrMdto> obterMedicamentosReceitaVO(Object strPesquisa) {
		return this.getFarmaciaON().obterMedicamentosReceitaVO(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterMedicamentosReceitaVOCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long obterMedicamentosReceitaVOCount(Object strPesquisa) {
		return this.getFarmaciaON().obterMedicamentosReceitaVOCount(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterAfaMensagemMedicamento(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AfaMensagemMedicamento obterAfaMensagemMedicamento(Integer seqMensagemMedicamento) {
		return this.getFarmaciaON().obterAfaMensagemMedicamento(seqMensagemMedicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#removerAfaMensagemMedicamento(br.gov.mec.aghu.model.AfaMensagemMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public void removerAfaMensagemMedicamento(Integer mensagemMedicamentoSeq) throws ApplicationBusinessException {
		this.getFarmaciaON().removerAfaMensagemMedicamento(mensagemMedicamentoSeq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaAfaMensagemMedicamentoCount(java.lang.Integer, java.lang.String, java.lang.Boolean, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisaAfaMensagemMedicamentoCount(Integer filtroSeq, String filtroDescricao,
			Boolean filtroCoexistente, DominioSituacao filtroSituacao) {
		return this.getFarmaciaON().pesquisaAfaMensagemMedicamentoCount(filtroSeq, filtroDescricao,
				filtroCoexistente, filtroSituacao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaAfaMensagemMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Integer, java.lang.String, java.lang.Boolean, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMensagemMedicamento> pesquisaAfaMensagemMedicamento(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, Integer filtroSeq,
			String filtroDescricao, Boolean filtroCoexistente, DominioSituacao filtroSituacao) {
		List<AfaMensagemMedicamento> lista = farmaciaON.pesquisaAfaMensagemMedicamento(firstResult, maxResults,
				orderProperty, asc, filtroSeq, filtroDescricao, filtroCoexistente, filtroSituacao);
		
		for (AfaMensagemMedicamento item : lista) {
			item.setGruposMedicamentosMensagem(afaGrupoMedicamentoMensagemDAO
					.pesquisarPorSeqMensagemMedicamento(item.getSeq()));
		}
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterAfaTipoVelocAdministracoes(java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public AfaTipoVelocAdministracoes obterAfaTipoVelocAdministracoes(
			Short seqVelocidadeAdministracao) {
		return this.getFarmaciaON().obterAfaTipoVelocAdministracoes(seqVelocidadeAdministracao);
	}

	@Override
	public void removerAfaTipoVelocAdministracoes(Short seq) throws BaseException {
		this.getFarmaciaON().removerAfaTipoVelocAdministracoes(seq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaAfaVelocidadesAdministracaoCount(java.lang.Short, java.lang.String, java.math.BigDecimal, java.lang.Boolean, java.lang.Boolean, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisaAfaVelocidadesAdministracaoCount(Short filtroSeq,
			String filtroDescricao, BigDecimal filtroFatorConversaoMlH, Boolean filtroTipoUsualNpt,
			Boolean filtroTipoUsualSoroterapia, DominioSituacao filtroSituacao) {
		return this.getFarmaciaON().pesquisaAfaVelocidadesAdministracaoCount(filtroSeq,
				filtroDescricao, filtroFatorConversaoMlH, filtroTipoUsualNpt,
				filtroTipoUsualSoroterapia, filtroSituacao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaAfaVelocidadesAdministracao(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Short, java.lang.String, java.math.BigDecimal, java.lang.Boolean, java.lang.Boolean, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoVelocAdministracoes> pesquisaAfaVelocidadesAdministracao(
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Short filtroSeq, String filtroDescricao, BigDecimal filtroFatorConversaoMlH,
			Boolean filtroTipoUsualNpt, Boolean filtroTipoUsualSoroterapia,
			DominioSituacao filtroSituacao) {
		return this.getFarmaciaON().pesquisaAfaVelocidadesAdministracao(firstResult, maxResults,
				orderProperty, asc, filtroSeq, filtroDescricao, filtroFatorConversaoMlH,
				filtroTipoUsualNpt, filtroTipoUsualSoroterapia, filtroSituacao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterAfaGrupoMedicamento(java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public AfaGrupoMedicamento obterAfaGrupoMedicamento(Short seqGrupoMedicamento) {
		return this.getFarmaciaON().obterAfaGrupoMedicamento(seqGrupoMedicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#removerAfaGrupoMedicamento(br.gov.mec.aghu.model.AfaGrupoMedicamento)
	 */
	@Override
	public void removerAfaGrupoMedicamento(Short seqAfaGrupoMedicamento) throws BaseException {
		this.getFarmaciaON().removerAfaGrupoMedicamento(seqAfaGrupoMedicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaAfaGrupoMedicamentoCount(java.lang.Short, java.lang.String, java.lang.Boolean, java.lang.Boolean, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisaAfaGrupoMedicamentoCount(Short filtroSeq, String filtroDescricao,
			Boolean filtroMedicamentoMesmoSal, Boolean filtroUsoFichaAnestesia,
			DominioSituacao filtroSituacao) {
		return this.getFarmaciaON().pesquisaAfaGrupoMedicamentoCount(filtroSeq, filtroDescricao,
				filtroMedicamentoMesmoSal, filtroUsoFichaAnestesia, filtroSituacao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaAfaGrupoMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Short, java.lang.String, java.lang.Boolean, java.lang.Boolean, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaGrupoMedicamento> pesquisaAfaGrupoMedicamento(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, Short filtroSeq,
			String filtroDescricao, Boolean filtroMedicamentoMesmoSal,
			Boolean filtroUsoFichaAnestesia, DominioSituacao filtroSituacao) {
		List<AfaGrupoMedicamento> afaGrupoMedicamentoList = this.getFarmaciaON().pesquisaAfaGrupoMedicamento(firstResult, maxResults,
				orderProperty, asc, filtroSeq, filtroDescricao, filtroMedicamentoMesmoSal,
				filtroUsoFichaAnestesia, filtroSituacao);
		for (AfaGrupoMedicamento item : afaGrupoMedicamentoList) {
			item.setItensGruposMedicamento(afaItemGrupoMedicamentoDAO
					.pesquisarPorSeqGrupoMedicamento(item.getSeq()));
		}
		return afaGrupoMedicamentoList;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#persistirAfaMensagemMedicamento(br.gov.mec.aghu.model.AfaMensagemMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public void persistirAfaMensagemMedicamento(AfaMensagemMedicamento mensagemMedicamento)
			throws BaseException {
		this.getFarmaciaON().persistirAfaMensagemMedicamento(mensagemMedicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaGruposMedicamentos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaGrupoMedicamento> pesquisaGruposMedicamentos(Object filtro) {
		return this.getFarmaciaON().pesquisaGruposMedicamentos(filtro);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisaGruposMedicamentosCount(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisaGruposMedicamentosCount(String filtro) {
		return this.getFarmaciaON().pesquisaGruposMedicamentosCount(filtro);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#persistirAfaGrupoMedicamento(br.gov.mec.aghu.model.AfaGrupoMedicamento)
	 */
	@Override
	public void persistirAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws BaseException {
		this.getFarmaciaON().persistirAfaGrupoMedicamento(grupoMedicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterTodosGruposUsoMedicamento()
	 */
	@Override
	@BypassInactiveModule
	public List<AfaGrupoUsoMedicamento> obterTodosGruposUsoMedicamento() {
		return this.getAfaGrupoUsoMedicamentoDAO().findAll();
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarOcorrencias(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Short, java.lang.String, br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoOcorDispensacao> pesquisarOcorrencias(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short seqOcorrencia, 
			String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia, DominioSituacao situacaoPesq){
		
		return getAfaTipoDispensacaoDAO().pesquisarOcorrencias(firstResult,
				maxResults, orderProperty, asc, seqOcorrencia,
				descricaoOcorrencia, tipoUsoOcorrencia, situacaoPesq);
		
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarOcorrenciasCount(java.lang.Short, java.lang.String, br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarOcorrenciasCount(Short seqOcorrencia, 
			String descricaoOcorrencia,
			DominioTipoUsoDispensacao tipoUsoOcorrencia, DominioSituacao situacaoPesq){
		
		return getAfaTipoDispensacaoDAO().pesquisarOcorrenciasCount(
				seqOcorrencia, descricaoOcorrencia, tipoUsoOcorrencia, situacaoPesq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterOcorrencia(java.lang.Short)
	 */
	@Override
	@BypassInactiveModule
	public AfaTipoOcorDispensacao obterOcorrencia(Short seq) {
		
		return getAfaTipoDispensacaoDAO().obterPorChavePrimaria(seq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarListaMedicamentos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarListaMedicamentos(Object strPesquisa) {
		return this.getAfaMedicamentoDAO().pesquisarTodosMedicamentos(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarListaMedicamentosCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarListaMedicamentosCount(Object strPesquisa) {
		return this.getAfaMedicamentoDAO().pesquisarTodosMedicamentosCount(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTodosGrupoUsoMedicamento(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaGrupoUsoMedicamento> pesquisarTodosGrupoUsoMedicamento(Object strPesquisa) {
		return this.getAfaGrupoUsoMedicamentoDAO().pesquisarTodosGrupoUsoMedicamento(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTodosGrupoUsoMedicamentoCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Integer pesquisarTodosGrupoUsoMedicamentoCount(Object strPesquisa) {
		return this.getAfaGrupoUsoMedicamentoDAO().pesquisarTodosGrupoUsoMedicamentoCount(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTodosTipoUsoMedicamento(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoUsoMdto> pesquisarTodosTipoUsoMedicamento(Object strPesquisa) {
		return this.getAfaTipoUsoMdtoDAO().pesquisarTodosTipoUsoMdto(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTodosTipoUsoMedicamentoCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Integer pesquisarTodosTipoUsoMedicamentoCount(Object strPesquisa) {
		return this.getAfaTipoUsoMdtoDAO().pesquisarTodosTipoUsoMdtoCount(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#verificarDelecao(java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public void verificarDelecao(Date data) throws ApplicationBusinessException {
		getFarmaciaRN().verificarDelecao(data);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMedicamentosPorTipoApresentacao(String sigla){
		return getAfaMedicamentoDAO().pesquisarMedicamentosPorTipoApresentacao(sigla);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterConteudoRelatorioPrescricaoPorUnidade(br.gov.mec.aghu.model.AghUnidadesFuncionais, java.util.Date, java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public List<PrescricaoUnidadeVO> obterConteudoRelatorioPrescricaoPorUnidade(
			AghUnidadesFuncionais unidadeFuncional, Date dataDeReferencia,
			String validade, Boolean indPmNaoEletronica) throws ApplicationBusinessException {
		return this.getRelatorioPrescricaoPorUnidadeON()
				.obterConteudoRelatorioPrescricaoPorUnidade(unidadeFuncional,
						dataDeReferencia, validade, indPmNaoEletronica);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterConteudoRelatorioMedicamentoPrescritoPorUnidade(java.util.Date, java.util.Date, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.AfaMedicamento, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoPrescritoUnidadeVO> obterConteudoRelatorioMedicamentoPrescritoPorUnidade(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim, AghUnidadesFuncionais unidadeFuncional, 
			AfaMedicamento medicamento, Boolean itemDispensado) throws ApplicationBusinessException {
		return this.getRelatorioMedicamentoPrescritoPorUnidadeON().
		obterConteudoRelatorioMedicamentoPrescritoPorUnidade(dataDeReferenciaInicio, 
				dataDeReferenciaFim, unidadeFuncional, medicamento, itemDispensado);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterConteudoRelatorioMedicamentosPrescritosPorPaciente(java.util.Date, java.util.Date, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.AfaMedicamento, br.gov.mec.aghu.model.AfaGrupoUsoMedicamento, br.gov.mec.aghu.model.AfaTipoUsoMdto, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoPrescritoPacienteVO> obterConteudoRelatorioMedicamentosPrescritosPorPaciente(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim, AghUnidadesFuncionais unidadeFuncional, 
			AfaMedicamento medicamento, AfaGrupoUsoMedicamento grupo, AfaTipoUsoMdto tipo, Boolean itemDispensado, Integer pacCodigo) throws ApplicationBusinessException {
		return this.getRelatorioMedicamentosPrescritosPorPacienteON().
		obterConteudoRelatorioMedicamentosPrescritosPorPaciente(dataDeReferenciaInicio, 
				dataDeReferenciaFim, unidadeFuncional, medicamento, grupo, tipo, itemDispensado, pacCodigo);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametro) {
		return getRelatorioPrescricaoPorUnidadeON().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(parametro);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#recuperarListaPaginadaLocalDispensacaoMdtos(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaLocalDispensacaoMdtos> recuperarListaPaginadaLocalDispensacaoMdtos(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getAfaLocalDispensacaoMdtosDAO().pesquisar(firstResult, maxResult, orderProperty, asc, medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarLocalDispensacaoMedicamentoCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarLocalDispensacaoMedicamentoCount(AfaMedicamento medicamento){

		return getAfaLocalDispensacaoMdtosDAO().pesquisarLocalDispensacaoMedicamentoCount(medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarDispensacaoMdtosListaPaginada(br.gov.mec.aghu.model.MpmPrescricaoMedica)
	 */
	/*@Override
	public Long pesquisarDispensacaoMdtosListaPaginada(MpmPrescricaoMedica prescricaoMedica, Short unfSeq) {
		return getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtosCount(prescricaoMedica, unfSeq);
	}*/
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
	protected AfaItemGrupoMedicamentoDAO getAfaItemGrupoMedicamentoDAO(){
		return afaItemGrupoMedicamentoDAO;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarUnidadesPmeInformatizadaByPesquisa(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> listarUnidadesPmeInformatizadaByPesquisa(
			Object parametro) {
		return getRelatorioMedicamentoPrescritoPorUnidadeON().listarUnidadesPmeInformatizadaByPesquisa(parametro);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarUnidadesPrescricaoByPesquisa(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> listarUnidadesPrescricaoByPesquisa(
			Object parametro) {
		return getRelatorioMedicamentosPrescritosPorPacienteON().listarUnidadesPrescricaoByPesquisa(parametro);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarItensDispensacaoMdtosCount(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario, String nomePaciente,
			Date dtHrInclusaoItem, AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao, AghUnidadesFuncionais farmacia,
			AghAtendimentos aghAtendimentos, MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica) {
		
		return getAfaDispensacaoMdtosDAO().pesquisarItensDispensacaoMdtosCount(
				unidadeSolicitante, prontuario, nomePaciente, dtHrInclusaoItem,
				medicamento, situacao, farmacia, aghAtendimentos,
				prescricaoMedica, loteCodigo, indPmNaoEletronica);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMdtosParaDispensacaoPorItemPrescrito(br.gov.mec.aghu.model.MpmItemPrescricaoMdto, br.gov.mec.aghu.model.MpmPrescricaoMedica)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMdtosParaDispensacaoPorItemPrescrito(MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica) {
		return getAfaMedicamentoDAO().pesquisarMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTipoOcorrenciasAtivasEstornadas()
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoOcorDispensacao> pesquisarTipoOcorrenciasAtivasEstornadas(Short ...seqsNotIn) {
		return getAfaTipoDispensacaoDAO().pesquisarTipoOcorrenciasAtivasEstornadas(seqsNotIn);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterConteudoRelatorioMedicamentoEstornadoPorMotivo(java.util.Date, java.util.Date, br.gov.mec.aghu.model.AghUnidadesFuncionais, br.gov.mec.aghu.model.AfaTipoOcorDispensacao, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoEstornadoMotivoVO> obterConteudoRelatorioMedicamentoEstornadoPorMotivo(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional,
			AfaTipoOcorDispensacao tipoOcorDispensacao,
			AfaMedicamento medicamento) throws ApplicationBusinessException {
		return getRelatorioMedicamentoEstornadoPorMotivoON()
				.obterConteudoRelatorioMedicamentoEstornadoPorMotivo(
						dataDeReferenciaInicio, dataDeReferenciaFim,
						unidadeFuncional, tipoOcorDispensacao, medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMotivoOcorrenciaDispensacao(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoOcorDispensacao> pesquisarMotivoOcorrenciaDispensacao(
			Object strPesquisa) {
		return getAfaTipoDispensacaoDAO().pesquisarMotivoOcorrenciaDispensacao(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMedicamentosPorCodigoDescricao(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMedicamentosPorCodigoDescricao(
			String parametro){
		return getAfaMedicamentoDAO().pesquisarMedicamentosPorCodigoDescricao(parametro);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMedicamentosCountPorCodigoDescricao(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarMedicamentosCountPorCodigoDescricao(String parametro){
		return getAfaMedicamentoDAO().pesquisarMedicamentosCountPorCodigoDescricao(parametro);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#gerarEtiquetas(br.gov.mec.aghu.model.SceLoteDocImpressao)
	 */
	@Override
	@BypassInactiveModule
	public String gerarEtiquetas(SceLoteDocImpressao entidade) throws ApplicationBusinessException {
		return getEtiquetasON().gerarEtiquetas(entidade);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarFormaDosagemJn(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaFormaDosagemJn> pesquisarFormaDosagemJn(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc, AfaMedicamento medicamento) {

		return this.getHistoricoFormaDosagemON().pesquisarFormaDosagemJn(firstResult, maxResult, orderProperty, asc,
				medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarFormaDosagemJnCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarFormaDosagemJnCount(AfaMedicamento medicamento) {

		return this.getHistoricoFormaDosagemON().pesquisarFormaDosagemJnCount(medicamento);
	
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterAfaFormaDosagem(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AfaFormaDosagem obterAfaFormaDosagem(Integer seq){
		return getAfaFormaDosagensDAO().obterPorChavePrimaria(seq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#atualizaAfaDispMdto(br.gov.mec.aghu.model.AfaDispensacaoMdtos, br.gov.mec.aghu.model.AfaDispensacaoMdtos)
	 */
	@Override
	@BypassInactiveModule
	public void atualizaAfaDispMdto(AfaDispensacaoMdtos admNew, AfaDispensacaoMdtos admOld, String nomeMicrocomputador) throws BaseException{
	
		getAfaDispensacaoMdtosRN().atualizaAfaDispMdto(admNew, admOld, nomeMicrocomputador);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#criaDispMdtoTriagemPrescricao(br.gov.mec.aghu.model.AfaDispensacaoMdtos)
	 */
	@Override
	@BypassInactiveModule
	public void criaDispMdtoTriagemPrescricao(AfaDispensacaoMdtos admNew, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		getAfaDispensacaoMdtosRN().criaDispMdtoTriagemPrescricao(admNew, nomeMicrocomputador);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getAfaDispOldDesatachado(br.gov.mec.aghu.model.AfaDispensacaoMdtos)
	 */
	@Override
	public AfaDispensacaoMdtos getAfaDispOldDesatachado(AfaDispensacaoMdtos adm) throws ApplicationBusinessException {
		return getAfaDispensacaoMdtosRN().getAfaDispOldDesatachado(adm);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#atribuirValidadeDaPrescricaoMedica(java.util.Date, br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	@BypassInactiveModule
	public String atribuirValidadeDaPrescricaoMedica(Date dataDeReferencia, AghUnidadesFuncionais unidadeFuncional) {
		return getRelatorioPrescricaoPorUnidadeON().atribuirValidadeDaPrescricaoMedica(dataDeReferencia, unidadeFuncional);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMedicamentoEquivalenteJn(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamentoEquivalenteJn> pesquisarMedicamentoEquivalenteJn(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc, AfaMedicamento medicamento) {
		return this.getHistoricoMedicamentoEquivalenteON().pesquisarMedicamentoEquivalenteJn(firstResult, maxResult, orderProperty, asc,
				medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMedicamentoEquivalenteJnCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarMedicamentoEquivalenteJnCount(AfaMedicamento medicamento) {
		return this.getHistoricoMedicamentoEquivalenteON().pesquisarMedicamentoEquivalenteJnCount(medicamento);
	
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#recuperarListaPaginadaLocalDispensacaoMdtosJn(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaLocalDispensacaoMdtosJn> recuperarListaPaginadaLocalDispensacaoMdtosJn(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getAfaLocalDispensacaoMdtosJnDAO()
				.pesquisarLocalDispensacaoMdtosJn(firstResult, maxResult,
						orderProperty, asc, medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarRelatorioQuantidadePrescricoesDispensacao(java.util.Date, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(
			Date dataEmissaoInicio, Date dataEmissaoFim)
			throws ApplicationBusinessException {
		return getRelatorioQuantidadePrescricoesDispensacaoON()
				.pesquisarRelatorioQuantidadePrescricoesDispensacao(
						dataEmissaoInicio, dataEmissaoFim);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarSinonimoMedicamentoJn(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaSinonimoMedicamentoJn> pesquisarSinonimoMedicamentoJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento) {
		return this.getHistoricoSinonimoMedicamentoON()
				.pesquisarSinonimoMedicamentoJn(firstResult, maxResult,
						orderProperty, asc, medicamento);

	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarSinonimoMedicamentoJnCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarSinonimoMedicamentoJnCount(
			AfaMedicamento medicamento) {
		return this.getHistoricoSinonimoMedicamentoON()
				.pesquisarSinonimoMedicamentoJnCount(medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarLocalDispensacaoMedicamentoCountJn(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarLocalDispensacaoMedicamentoCountJn(
			AfaMedicamento medicamento) {
		return getAfaLocalDispensacaoMdtosJnDAO()
		.pesquisarLocalDispensacaoMdtosJnCount(medicamento);
	}
	
	//Getters e setters
	
	private AfaDispensacaoMdtosRN getAfaDispensacaoMdtosRN(){
		return afaDispensacaoMdtosRN;
	}
	
	protected AfaTipoOcorDispensacaoDAO getAfaTipoDispensacaoDAO(){
		return afaTipoOcorDispensacaoDAO;
	}
	
	protected AfaMedicamentoDAO getAfaMedicamentoDAO() {
		return afaMedicamentoDAO;
	}
	
	protected CadastroDiluentesON getCadastroDiluentesON() {
		return cadastroDiluentesON;
	}
	
	protected FarmaciaON getFarmaciaON() {
		return farmaciaON;
	}	
	
	protected FarmaciaRN getFarmaciaRN() {
		return farmaciaRN;
	}
	
	protected AfaGrupoUsoMedicamentoDAO getAfaGrupoUsoMedicamentoDAO() {
		return afaGrupoUsoMedicamentoDAO;
	}

	protected AfaTipoUsoMdtoDAO getAfaTipoUsoMdtoDAO() {
		return afaTipoUsoMdtoDAO;
	}

	protected AfaFormaDosagemDAO getAfaFormaDosagensDAO() {
		return afaFormaDosagemDAO;
	}

	protected AfaViaAdministracaoDAO getAfaViaAdministracaoDAO() {
		return afaViaAdministracaoDAO;
	}

	protected AfaLocalDispensacaoMdtosDAO getAfaLocalDispensacaoMdtosDAO(){
		return afaLocalDispensacaoMdtosDAO;
	}
	
	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}	
	
	protected HistoricoFormaDosagemON getHistoricoFormaDosagemON() {
		return historicoFormaDosagemON;
	}
	
	private RelatorioMedicamentoEstornadoPorMotivoON getRelatorioMedicamentoEstornadoPorMotivoON(){
		return relatorioMedicamentoEstornadoPorMotivoON;
	}
	
	protected RelatorioPrescricaoPorUnidadeON getRelatorioPrescricaoPorUnidadeON() {
		return relatorioPrescricaoPorUnidadeON;
	}	
	
	protected RelatorioMedicamentoPrescritoPorUnidadeON getRelatorioMedicamentoPrescritoPorUnidadeON() {
		return relatorioMedicamentoPrescritoPorUnidadeON;
	}
	
	protected RelatorioMedicamentosPrescritosPorPacienteON getRelatorioMedicamentoPrescritoPorPacienteON() {
		return relatorioMedicamentosPrescritosPorPacienteON;
	}
	
	protected RelatorioMedicamentosPrescritosPorPacienteON getRelatorioMedicamentosPrescritosPorPacienteON() {
		return relatorioMedicamentosPrescritosPorPacienteON;
	}
		
	protected HistoricoMedicamentoEquivalenteON getHistoricoMedicamentoEquivalenteON() {
		return historicoMedicamentoEquivalenteON;
	}
	
	protected AfaLocalDispensacaoMdtosJnDAO getAfaLocalDispensacaoMdtosJnDAO() {
		return afaLocalDispensacaoMdtosJnDAO;
	}
	
	private RelatorioQuantidadePrescricoesDispensacaoON getRelatorioQuantidadePrescricoesDispensacaoON() {
		return relatorioQuantidadePrescricoesDispensacaoON;
	}
	
	protected HistoricoSinonimoMedicamentoON getHistoricoSinonimoMedicamentoON() {
		return historicoSinonimoMedicamentoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarViasAdministracaoJn(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracaoMedicamentoJN> pesquisarViasAdministracaoJn(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento) {
		return getHistoricoViasAdministracaoON().pesquisarJn(firstResult, maxResult, orderProperty, asc, medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarViasAdministracaoJnCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarViasAdministracaoJnCount(AfaMedicamento medicamento) {
		return getHistoricoViasAdministracaoON().pesquisarJnCount(medicamento);
	}
	
	private HistoricoViasAdministracaoON getHistoricoViasAdministracaoON(){
		return historicoViasAdministracaoON;
	}
	
	//Estória # 5697
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterListaTodosMedicamentos()
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoSinteticoVO> obterListaTodosMedicamentos() 
	throws ApplicationBusinessException {
		return this.getRelatorioMedicamentoSinteticoON().obterListaTodosMedicamentos();
	}
	
	//Estória # 5697
	private RelatorioMedicamentoSinteticoON getRelatorioMedicamentoSinteticoON() {
		return relatorioMedicamentoSinteticoON;
	}	
	
	// HISTORICO CADASTRO DE MEDICAMENTO
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarHistoricoCadastroMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamentoJn> pesquisarHistoricoCadastroMedicamento(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc, AfaMedicamento medicamento) {
		return this.getHistoricoCadastroMedicamentoON().pesquisarHistoricoCadastroMedicamento(firstResult, maxResult, orderProperty, asc,
				medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarHistoricoCadastroMedicamentoCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarHistoricoCadastroMedicamentoCount(AfaMedicamento medicamento) {
		return this.getHistoricoCadastroMedicamentoON().pesquisarHistoricoCadastroMedicamentoCount(medicamento);
	
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterHistoricoCadastroMedicamento(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public HistoricoCadastroMedicamentoVO obterHistoricoCadastroMedicamento(Integer seqJn)  throws ApplicationBusinessException {

		return getHistoricoCadastroMedicamentoON().obterHistoricoCadastroMedicamento(seqJn);
	}
	
	protected HistoricoCadastroMedicamentoON getHistoricoCadastroMedicamentoON() {
		return historicoCadastroMedicamentoON;
	}
	
	private AfaTipoApresentacaoMedicamentoDAO getAfaAfaTipoApresentacaoMedicamentoDAO(){
		return afaTipoApresentacaoMedicamentoDAO;
	}
	
	///Estória # 5714
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterListaMedicamentosDispensadosPorBox(java.util.Date, java.util.Date, br.gov.mec.aghu.model.AghMicrocomputador, br.gov.mec.aghu.model.AfaMedicamento, br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento, br.gov.mec.aghu.model.AfaGrupoApresMdtos)
	 */
	@Override
	@BypassInactiveModule
	public List<MedicamentoDispensadoPorBoxVO> obterListaMedicamentosDispensadosPorBox(
			Date dataEmissaoInicio, 
			Date dataEmissaoFim, 
			AghMicrocomputador aghMicrocomputador,
			AfaMedicamento medicamento,
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento,
			AfaGrupoApresMdtos afaGrupoApresMdtos, Integer pacCodigo
			)throws ApplicationBusinessException {
		
		return getRelatorioMedicamentoDispensadoPorBoxON().obterListaMedicamentosDispensadosPorBox(
				dataEmissaoInicio, 
				dataEmissaoFim,
				aghMicrocomputador,
				medicamento,
				tipoApresentacaoMedicamento,
				afaGrupoApresMdtos, pacCodigo);			
	}

	//#5714
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTipoApresMdtos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoApresentacaoMedicamento> pesquisarTipoApresMdtos(
			Object strPesquisa) {
		return this.getAfaAfaTipoApresentacaoMedicamentoDAO().pesquisaTipoApresentacaoMedicamentosAtivos(strPesquisa);
	}
	
	//#5714
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarGrupoApresMdtos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaGrupoApresMdtos> pesquisarGrupoApresMdtos(
			Object strPesquisa) {
		return this.getAfaGrupoApresMdtosDAO().pesquisarGrupoApresMdtos(strPesquisa);
	}
	
	//#5714
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarGrupoApresMdtosCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarGrupoApresMdtosCount(Object strPesquisa) {
		return this.getAfaGrupoApresMdtosDAO().pesquisarGrupoApresMdtosCount(strPesquisa);
	}
	
	//#5714
	private AfaGrupoApresMdtosDAO getAfaGrupoApresMdtosDAO() {
		
		return afaGrupoApresMdtosDAO;
	}
	
	//Estória # 5714
	private RelatorioMedicamentoDispensadoPorBoxON getRelatorioMedicamentoDispensadoPorBoxON() {
		return relatorioMedicamentoDispensadoPorBoxON;
	}
	//Estória # 5714
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterCustoMedioPonderado(java.lang.Integer, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public BigDecimal obterCustoMedioPonderado (Integer codigoScoMaterial, Date dataCompetencia) throws ApplicationBusinessException{
		return getRelatorioMedicamentoPrescritoPorUnidadeRN().obterCustoMedioPonderado(codigoScoMaterial, dataCompetencia);
	}
	//Estória # 5714
	private RelatorioMedicamentoPrescritoPorUnidadeRN getRelatorioMedicamentoPrescritoPorUnidadeRN(){
		return relatorioMedicamentoPrescritoPorUnidadeRN;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#mpmpGeraDispTot(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public void mpmpGeraDispTot(Integer pmeAtdSeq, Integer pmeSeq, Date dthrInicio,
			Date dthrFim, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		getAfaDispensacaoMdtosRN().mpmpGeraDispTot(pmeAtdSeq, pmeSeq, dthrInicio, dthrFim, nomeMicrocomputador);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#mpmpGeraDispMVto(java.lang.Integer, java.lang.Integer, java.util.Date, java.util.Date, java.util.Date)
	 */
	@Override
	@BypassInactiveModule
	public void mpmpGeraDispMVto(Integer pmeAtdSeq, Integer pmeSeq,
			Date dthrInicioMvtoPendente, Date dthrInicio, Date dthrFim, String nomeMicrocomputador) throws BaseException, ApplicationBusinessException {
				
		getAfaDispensacaoMdtosRN().mpmpGeraDispMVto(pmeAtdSeq, pmeSeq, dthrInicioMvtoPendente, dthrInicio, dthrFim, nomeMicrocomputador);
		
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getMpmcOperMvto(java.util.Date, java.lang.Integer, java.lang.Long)
	 */
	@Override
	@BypassInactiveModule
	public TipoOperacaoEnum getMpmcOperMvto(Date pmdAlteradoEm, Integer pmdAtdSeq, Long pmdSeq) {
		return getAfaDispensacaoMdtosRN().mpmcGetOperMvto(pmdAlteradoEm, pmdAtdSeq, pmdSeq);
	}
	
	//Estória # 5388
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#getDominioSituacaoDispensacaoMdto()
	 */
	@Override
	@BypassInactiveModule
	public List<DominioSituacaoDispensacaoMdto> getDominioSituacaoDispensacaoMdto(){
		return Arrays.asList(DominioSituacaoDispensacaoMdto.S, DominioSituacaoDispensacaoMdto.T, DominioSituacaoDispensacaoMdto.D);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMdtosParaDispensacaoPorItemPrescrito(br.gov.mec.aghu.model.MpmItemPrescricaoMdto, br.gov.mec.aghu.model.MpmPrescricaoMedica, java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMdtosParaDispensacaoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa) {
		return getAfaMedicamentoDAO().pesquisarMdtosParaDispensacaoPorItemPrescrito(itemPrescrito, prescricaoMedica, strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarMdtosParaDispensacaoPorItemPrescritoCount(br.gov.mec.aghu.model.MpmItemPrescricaoMdto, br.gov.mec.aghu.model.MpmPrescricaoMedica, java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarMdtosParaDispensacaoPorItemPrescritoCount(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Object strPesquisa) {
		return 	getAfaMedicamentoDAO().pesquisarMdtosParaDispensacaoPorItemPrescritoCount(itemPrescrito, prescricaoMedica, strPesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(final Short unfSeq, final String filtro, final boolean firstQuery) {
		return 	getAfaMedicamentoDAO().pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(unfSeq, filtro, firstQuery);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuaisCount(final Short unfSeq, final String filtro, boolean firstQuery) {
		return 	getAfaMedicamentoDAO().pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuaisCount(unfSeq, filtro, firstQuery);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(Object parametroPesquisa) {

		return getFarmaciaON().pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(parametroPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarTipoApresMdtosCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarTipoApresMdtosCount(Object strPesquisa) {
		return this.getAfaAfaTipoApresentacaoMedicamentoDAO().pesquisaTipoApresentacaoMedicamentosAtivosCount(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
			String strPesquisa) {
		return getFarmaciaON().pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarFarmaciasAtivasByPesquisa(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(
			Object strPesquisa) {
		return getFarmaciaON().listarFarmaciasAtivasByPesquisa(strPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#listarFarmaciasAtivasByPesquisaCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long listarFarmaciasAtivasByPesquisaCount(Object strPesquisa) {
		return getFarmaciaON().listarFarmaciasAtivasByPesquisaCount(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterAfaTipoApresentacaoMedicamentoPorId(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public AfaTipoApresentacaoMedicamento obterAfaTipoApresentacaoMedicamentoPorId(String tprSigla) {
		return getAfaAfaTipoApresentacaoMedicamentoDAO().obterPorChavePrimaria(tprSigla);
		
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterAfaGrupoMedicamentoComItemGrupoMdto(java.lang.Integer, java.lang.Boolean)
	 */
	@Override
	@BypassInactiveModule
	public AfaGrupoMedicamento obterAfaGrupoMedicamentoComItemGrupoMdto(
			Integer matCodigo, Boolean usoFichaAnestesia) {
		return getAfaGrupoMedicamentoDAO().obterAfaGrupoMedicamentoComItemGrupoMdto(matCodigo, usoFichaAnestesia);
	}
	
	private AfaGrupoMedicamentoDAO getAfaGrupoMedicamentoDAO(){
		return afaGrupoMedicamentoDAO;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.business.IFarmaciaFacade#obterLocalizacaoPacienteParaRelatorio(br.gov.mec.aghu.model.AghAtendimentos)
	 */
	//----

	@Override
	@BypassInactiveModule
	public String obterLocalizacaoPacienteParaRelatorio(AghAtendimentos atendimento) {
		return getRelatorioPrescricaoPorUnidadeRN().obterLocalizacaoPacienteParaRelatorio(atendimento);
	}
	
	protected RelatorioPrescricaoPorUnidadeRN getRelatorioPrescricaoPorUnidadeRN(){
		return relatorioPrescricaoPorUnidadeRN;
	}
	
	protected AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO(){
		return afaDispMdtoCbSpsDAO;
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificaExclusao(MpmUnidadeTempo unidadeTempo) {
		return getAfaTempoEstabilidadesDAO().verificaExclusao(unidadeTempo);
	}
	
	protected AfaTempoEstabilidadesDAO getAfaTempoEstabilidadesDAO(){
		return afaTempoEstabilidadesDAO;
	}
	
	@Override
	@BypassInactiveModule
	public Long listarUnidadesPrescricaoByPesquisaCount(
			Object parametro) {
		return getRelatorioMedicamentosPrescritosPorPacienteON().listarUnidadesPrescricaoByPesquisaCount(parametro);
	}

	@Override
	@BypassInactiveModule
	public Long listarUnidadesPmeInformatizadaByPesquisaCount(
			Object parametro) {
		return getRelatorioMedicamentoPrescritoPorUnidadeON().listarUnidadesPmeInformatizadaByPesquisaCount(parametro);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
			Object parametro) {
		return getRelatorioPrescricaoPorUnidadeON().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(parametro);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarMotivoOcorrenciaDispensacaoCount(
			Object strPesquisa) {
		return getAfaTipoDispensacaoDAO().pesquisarMotivoOcorrenciaDispensacaoCount(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public Boolean verificarSeMedicamentoPossuiViaSiglaDiferente(String sigla,
			Integer matCodigo) {
		return getAfaViaAdministracaoMedicamentoDAO().verificarSeMedicamentoPossuiViaSiglaDiferente(sigla, matCodigo);
	}
	
	private AfaViaAdministracaoMedicamentoDAO getAfaViaAdministracaoMedicamentoDAO(){
		return afaViaAdministracaoMedicamentoDAO;
	}

	@Override
	@BypassInactiveModule
	public AfaGrupoUsoMedicamento obterAfaGrupoUsoMedicamentoPorChavePrimaria(Integer seq) {
		return getAfaGrupoUsoMedicamentoDAO().obterPorChavePrimaria(seq);
	}
	
	private AfaTipoVelocAdministracoesDAO getAfaTipoVelocAdministracoesDAO() {
		return afaTipoVelocAdministracoesDAO;
	}

	@Override
	@BypassInactiveModule
	public AfaTipoVelocAdministracoes obterAfaTipoVelocAdministracoesDAO(Short seq) {
		return getAfaTipoVelocAdministracoesDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public DominioSituacao obtemSituacaoTipoVelocidade(Short seq) {
		return getAfaTipoVelocAdministracoesDAO().obtemSituacaoTipoVelocidade(seq);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> listarViasAdministracao(final Object param) {
		return getAfaViaAdministracaoDAO().obterViaAdminstracaoSiglaouDescricao(param);
	}

	@Override
	public List<ViaAdministracaoVO> pesquisarViaAdminstracaoSiglaouDescricao(String param) {
		return farmaciaON.pesquisarViaAdminstracaoSiglaouDescricao(param);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarViasAdministracaoCount(String sigla, String descricao, DominioSituacao situacao) {
		return getAfaViaAdministracaoDAO().pesquisarViasAdministracaoCount(sigla, descricao, situacao);
	}

	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> pesquisarViasAdministracao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String sigla, String descricao,
			DominioSituacao situacao) {
		return getAfaViaAdministracaoDAO().pesquisarViasAdministracao(firstResult, maxResult, orderProperty, asc, sigla, descricao, situacao);
	}

	@SuppressWarnings("deprecation")
	@Override
	@BypassInactiveModule
	public void removerViaAdministracao(AfaViaAdministracao viaAdministracao) {
		getAfaViaAdministracaoDAO().remover(viaAdministracao);
		getAfaViaAdministracaoDAO().flush();
	}

	@Override
	@BypassInactiveModule
	public boolean existeSiglaCadastrada(String sigla) {
		return getAfaViaAdministracaoDAO().existeSiglaCadastrada(sigla);
	}

	@Override
	@SuppressWarnings("rawtypes")
	@BypassInactiveModule
	public boolean existeItemViaAdministracao(AfaViaAdministracao viaAdministracao, Class class1, Enum field) {
		return getAfaViaAdministracaoDAO().existeItemViaAdministracao(viaAdministracao, class1, field);
	}

	@SuppressWarnings("deprecation")
	@Override
	public AfaViaAdministracao inserirViaAdministracao(
			AfaViaAdministracao viaAdministracao) {
		getAfaViaAdministracaoDAO().persistir(viaAdministracao);
		getAfaViaAdministracaoDAO().flush();
		return viaAdministracao;
	}

	@SuppressWarnings("deprecation")
	@Override
	@BypassInactiveModule
	public AfaViaAdministracao atualizarViaAdministracao(
			AfaViaAdministracao viaAdministracao, boolean flush) {
		AfaViaAdministracao retorno = getAfaViaAdministracaoDAO().atualizar(
				viaAdministracao);
		if (flush) {
			getAfaViaAdministracaoDAO().flush();
		}
		return retorno;
	}

	@Override
	@BypassInactiveModule
	public AfaViaAdministracaoMedicamento obterViaAdministracaoMedicamento(AfaViaAdministracaoMedicamentoId id) {
		return getAfaViaAdministracaoMedicamentoDAO().obterPorChavePrimaria(id);
	}
	
	protected AfaViaAdmUnfDAO getAfaViaAdmUnfDAO() {
		return afaViaAdmUnfDAO;
	}

	@Override
	@BypassInactiveModule
	public List<AfaViaAdmUnf> listarAfaViaAdmUnfAtivasPorUnidadeFuncional(AghUnidadesFuncionais unidade) {
		return getAfaViaAdmUnfDAO().listarAfaViaAdmUnfAtivasPorUnidadeFuncional(unidade);
	}

	@Override
	@BypassInactiveModule
	public List<AfaViaAdmUnf> listarAfaViaAdmUnfAtivasPorUnidadeFuncionalEViaAdministracao(
			AghUnidadesFuncionais unidade, AfaViaAdministracao viaAdministracao) {
		return getAfaViaAdmUnfDAO().listarAfaViaAdmUnfAtivasPorUnidadeFuncionalEViaAdministracao(unidade, viaAdministracao);
	}
	
	@Override
	@BypassInactiveModule
	public AfaViaAdmUnf obterViaAdmUnfId(String sigla, Short unfSeq) {
		return getAfaViaAdmUnfDAO().recuperarAfaViaAdmUnfPorId(sigla, unfSeq);
	}

	@Override
	@BypassInactiveModule
	public List<AfaViaAdmUnf> listarViaAdmUnf(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AghUnidadesFuncionaisVO unidFuncionais) {
		return getAfaViaAdmUnfDAO().listarAfaViaAdmUnf(firstResult, maxResult, orderProperty, asc, unidFuncionais);
	}
	
	@Override
	public List<AfaViaAdmUnfVO> listarViaAdmUnfVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidFuncionais) {
		return getFarmaciaRN().listarViaAdmUnfVO(firstResult, maxResult,
				orderProperty, asc, unidFuncionais);
	}

	@Override
	@BypassInactiveModule
	public Long listarViaAdmUnfCount(AghUnidadesFuncionaisVO unidFuncionais) {
		return getAfaViaAdmUnfDAO().listarViaAdmUnfCount(unidFuncionais);
	}
		
	@Override
	@BypassInactiveModule
	public List<VAfaDescrMdto> obtemListaDescrMdtoAtivos(final Object parametro) {
		return getVAfaDescrMdtoDAO().obtemListaDescrMdtoAtivos(parametro);
	}
	
	@Override
	@BypassInactiveModule
	public Long obtemListaDescrMdtoAtivosCount(final Object parametro) {
		return getVAfaDescrMdtoDAO().obtemListaDescrMdtoAtivosCount(parametro);
	}
		
	private VAfaDescrMdtoDAO getVAfaDescrMdtoDAO() {
		return vAfaDescrMdtoDAO;
	}
	
	private AfaTempoEstabilidadesJnDAO getAfaTempoEstabilidadesJnDAO(){
		return afaTempoEstabilidadesJnDAO;
	}

	@Override
	@BypassInactiveModule
	public Boolean verificaExclusaoJN(MpmUnidadeTempo unidadeTempo) {
		return getAfaTempoEstabilidadesJnDAO().verificaExclusao(unidadeTempo);
	}

	@Override
	@BypassInactiveModule
	public List<Short> listarOrdemSumarioPrecricao(Integer ituSeq) {
		return getAfaItemGrupoMedicamentoDAO().listarOrdemSumarioPrescricao(ituSeq);
	}
	
	@Override	
	@BypassInactiveModule
	public void gerarCSVInterfaceamentoImpressoraGriffols(UnitarizacaoVO unitarizacaoVo, Long quantidade) throws ApplicationBusinessException{
		getFarmaciaON().gerarCSVInterfaceamentoImpressoraGriffols(unitarizacaoVo, quantidade);
	}

	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMedicamentosAtivos(Object objPesquisa) {
		return this.getAfaMedicamentoDAO().pesquisarMedicamentosAtivos(objPesquisa);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarMedicamentosAtivosCount(Object objPesquisa) {
		return this.getAfaMedicamentoDAO().pesquisarMedicamentosAtivosCount(objPesquisa);
	}
	
	@Override 
	@BypassInactiveModule
	public AfaDispensacaoMdtos obterDispensacaoMdtosPorItemPrescricaoMdtoQtdSolicitada(MpmItemPrescricaoMdtoId itemPrescricaoMdtoId){
		return getAfaDispensacaoMdtosDAO().obterDispensacaoMdtosPorItemPrescricaoMdtoQtdSolicitada(itemPrescricaoMdtoId);
	}

	@Override 
	public Boolean existeDispensacaoAnteriorPacienteUTI(Integer atdSeq, Short unfSeq) {
		return getAfaDispensacaoMdtosDAO().existeDispensacaoAnteriorPacienteUTI(atdSeq, unfSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracao> listarTodasAsVias(String strPesquisa) {
		return getAfaViaAdministracaoDAO().listarTodasAsVias(strPesquisa);
	}

	@Override
	@BypassInactiveModule
	public List<AfaViaAdmUnf> listarAfaViaAdmUnfPorUnidadeFuncional(AghUnidadesFuncionais unidade) {
		return getAfaViaAdmUnfDAO().listarAfaViaAdmUnfPorUnidadeFuncional(unidade);
	}

	public List<AfaMedicamento> pesquisarMedicamentosParaMedicamentosCuidados(String parametro) {
		return getAfaMedicamentoDAO().pesquisarMedicamentosParaMedicamentosCuidados(parametro);
	}

	public Long pesquisarMedicamentosParaMedicamentosCuidadosCount(String parametro) {
		return getAfaMedicamentoDAO().pesquisarMedicamentosParaMedicamentosCuidadosCount(parametro);
	}

	public List<AfaMedicamento> pesquisarMedicamentosParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return getAfaMedicamentoDAO().pesquisarMedicamentosParaListagemMedicamentosCuidados(matCodigo, indSituacao, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(Integer matCodigo, DominioSituacaoMedicamento indSituacao) {
		return getAfaMedicamentoDAO().pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(matCodigo, indSituacao);
	}

	public AfaMedicamento obterMedicamentoPorMatCodigo(Integer matCodigo) {
		return getAfaMedicamentoDAO().obterMedicamentoPorMatCodigo(matCodigo);	
	}

	@Override
	@BypassInactiveModule
	public List<SigPreparoMdtoVO> buscarBolsasSeringasDinpensacoes(
			Date dataInicioProcessamento, Date dataFimProcessamento,
			Integer tipoTratamento) {
		return this.getAfaPreparoMdtosDAO().buscarBolsasSeringasDinpensacoes(dataInicioProcessamento, dataFimProcessamento, tipoTratamento);
	}
	
	@Override
	@BypassInactiveModule
	public AfaMedicamento obterMedicamentoOriginal(Integer matCodigo) {
		return getAfaMedicamentoDAO().obterOriginal(matCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracaoMedicamento> pesquisarAfaViaAdministracaoMedicamento(AfaMedicamento medicamento){
		return getAfaViaAdministracaoMedicamentoDAO().pesquisar(medicamento);
	}

	@Override
	@BypassInactiveModule
	public AfaMedicamento obterMedicamentoComUnidadeMedidaMedica(Integer codigo) {
		return getAfaMedicamentoDAO().obterMedicamentoComUnidadeMedidaMedica(codigo);
	}

	@Override
	@BypassInactiveModule
	public AfaGrupoMedicamento obterAfaGrupoMedicamentoComItemGrupoMdto(Short seqAfaGrupoMedicamento) {
		return getAfaGrupoMedicamentoDAO().obterAfaGrupoMedicamentoComItemGrupoMdto(seqAfaGrupoMedicamento);
	}

	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMedicamentoAtivoPorCodigoOuDescricao(String parametro, Integer maxResults) {
		return getAfaMedicamentoDAO().pesquisarMedicamentoAtivoPorCodigoOuDescricao(parametro, maxResults);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarMedicamentoAtivoPorCodigoOuDescricaoCount(String parametro) {
		return getAfaMedicamentoDAO().pesquisarMedicamentoAtivoPorCodigoOuDescricaoCount(parametro);
	}
	
	
	@Override
	@BypassInactiveModule
	public List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacaoUnion2(
			String leitoId, Integer prontuario, String nome, Date dtHrInicio,
			Date dtHrFim, String seqPrescricao, Boolean indPacAtendimento) {
		
		return afaPrescricaoMedicamentoDAO.listarPrescricaoMedicaSituacaoDispensacaoUnion2(leitoId, prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao, indPacAtendimento);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMedicamentoPorCodigos(List<Integer> matCodigos) {
		return getAfaMedicamentoDAO().pesquisarMedicamentoPorCodigos(matCodigos);
	}
	
	@Override	
	@BypassInactiveModule
	public void gerarCSVInterfaceamentoImpressoraHujf(UnitarizacaoVO unitarizacaoVo, Long quantidade) throws ApplicationBusinessException {
		getFarmaciaON().gerarCSVInterfaceamentoImpressoraHujf(unitarizacaoVo, quantidade);
	}
	
	@Override
	public void excluir(AfaViaAdmUnfVO viaVinculadoUnidade,
			RapServidores servidor) throws ApplicationBusinessException {
		getFarmaciaRN().excluirRN(viaVinculadoUnidade, servidor);
	}
	
	@Override
	public void incluirTodasViasUnf(AghUnidadesFuncionais unidFuncionais,
			RapServidores servidor) throws ApplicationBusinessException {
		getFarmaciaRN().incluirTodasViasUnf(unidFuncionais, servidor);
	}

	@Override
	public void incluirViasUnfs(AghUnidadesFuncionais unidFuncionais,
			RapServidores servidor) throws ApplicationBusinessException {
		getFarmaciaRN().incluirViasUnfs(unidFuncionais, servidor);
	}

	@Override
	@BypassInactiveModule
	public List<ComposicaoItemPreparoVO> pesquisarComposicaoItemPreparo(Integer itoPtoSeq, Short itoSeqp) {
		
		return this.getAfaComposicaoItemPreparoDAO().pesquisarComposicaoItemPreparo(itoPtoSeq, itoSeqp);
	}
	
	@Override
	@BypassInactiveModule
	public List<SigPreparoMdtoPrescricaoMedicaVO> buscarBolsasSeringasDinpensacoesPrescricaoMedica(
			Date dataInicio, Date dataFim) {
		return this.getAfaPreparoMdtosDAO().buscarBolsasSeringasDinpensacoesPrescricaoMedica(dataInicio, dataFim);
	}
	
	public AfaPreparoMdtosDAO getAfaPreparoMdtosDAO() {
		return afaPreparoMdtosDAO;
	}

	
	private AfaComposicaoItemPreparoDAO getAfaComposicaoItemPreparoDAO(){
		return afaComposicaoItemPreparoDAO;
	}
	
//	@Override
//	@BypassInactiveModule
//	public void imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(SceLoteDocImpressao loteDocImpressao, 
//			RapServidores servidorLogado,String nomeMicrocomputador, Integer qtdeEtiquetas) 
//				throws BaseException {
//		
///*TODO Merge */
////		getEtiquetasON()
////				.imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(
////						loteDocImpressao, servidorLogado, nomeMicrocomputador,
////						qtdeEtiquetas);
//	}
	
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> pesquisarPorDescricaoCodigoAtivaAssociada(String strPesquisa) {
		return pesquisaPacientesEmAtendimentoON.pesquisarPorDescricaoCodigoAtivaAssociada(strPesquisa);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarPorDescricaoCodigoAtivaAssociadaCount(String strPesquisa) {
		return pesquisaPacientesEmAtendimentoON.pesquisarPorDescricaoCodigoAtivaAssociadaCount(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public List<MedicoResponsavelVO> pesquisarMedicoResponsavel(String strPesquisa,Integer matriculaResponsavel, Short vinCodigoResponsavel) {
		return pesquisaPacientesEmAtendimentoON.pesquisarMedicoResponsavel(strPesquisa, matriculaResponsavel, vinCodigoResponsavel);
	}
	
	@Override
	@BypassInactiveModule
	public List<PacientesEmAtendimentoVO> listarPacientesEmAtendimento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Integer pacCodigo, String leito, Short unfSeq,
			Integer matriculaResp, Short vinCodigoResp,
			DominioOrigemAtendimento origem, Short espSeq) throws ApplicationBusinessException {
		return pesquisaPacientesEmAtendimentoON.listarPacientesEmAtendimento(
				firstResult, maxResult, orderProperty, asc, pacCodigo, leito, unfSeq,
				matriculaResp, vinCodigoResp, origem, espSeq);
	}

	@Override
	@BypassInactiveModule
	public Long listarPacientesEmAtendimentoCount(Integer pacCodigo,
			String leito, Short unfSeq, Integer matriculaResp,
			Short vinCodigoResp, DominioOrigemAtendimento origem, Short espSeq) throws ApplicationBusinessException {
		return pesquisaPacientesEmAtendimentoON.listarPacientesEmAtendimentoCount(pacCodigo, leito, unfSeq, matriculaResp, vinCodigoResp, origem, espSeq);
	}
	
	@Override
	@BypassInactiveModule
	public AfaPrescricaoMedicamento obterAfaPrescricaoMedicamento(
			Long seqAfaPrescricaoMedicamento) {
		return afaPrescricaoMedicamentoDAO.obterPorChavePrimaria(seqAfaPrescricaoMedicamento);
	}
	
	@Override
	@BypassInactiveModule
	public AfaPrescricaoMedicamento obterAfaPrescricaoMedicamento(
			Long seqAfaPrescricaoMedicamento, Boolean left, Enum... fetchArgs) {
		return afaPrescricaoMedicamentoDAO.obterPorChavePrimaria(seqAfaPrescricaoMedicamento, left, fetchArgs);
	}
	
	@Override
	@BypassInactiveModule
	public AfaDispensacaoMdtos processaNovaAfaDispMdto(Integer atdSeq, Integer pmeSeq,
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo, Short imeSeqp, 
			Boolean prescricaoMedicamentoIndSeNecessario, BigDecimal dose,
			BigDecimal percSeNecessario, Integer fdsSeq,
			DominioSituacaoItemPrescritoDispensacaoMdto indSitItemPrescrito, Long dsmSequencia,
			Boolean indPmNaoEletronica, Long prmSeq) throws ApplicationBusinessException {
		
		return getAfaDispensacaoMdtosRN().processaNovaAfaDispMdto(atdSeq, pmeSeq,
				pmdAtdSeq, pmdSeq, medMatCodigo, imeSeqp,
				prescricaoMedicamentoIndSeNecessario, dose, percSeNecessario,
				fdsSeq, indSitItemPrescrito, dsmSequencia, indPmNaoEletronica,
				prmSeq);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarMedicamentos(Object objPesquisa,
			DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento, DominioSituacao situacaoTipoApresentacaoMedicamento,
			String ... orders) {
		return getAfaMedicamentoDAO().pesquisarMedicamentos(objPesquisa,
				situacaoMedicamento, joinTipoApresentacaoMedicamento,
				situacaoTipoApresentacaoMedicamento, orders);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarMedicamentosCount(Object objPesquisa,
			DominioSituacao situacaoMedicamento,
			Boolean joinTipoApresentacaoMedicamento, DominioSituacao situacaoTipoApresentacaoMedicamento) {
		return getAfaMedicamentoDAO().pesquisarMedicamentosCount(objPesquisa,
				situacaoMedicamento, joinTipoApresentacaoMedicamento,
				situacaoTipoApresentacaoMedicamento);
	}
	
	@Override
	@BypassInactiveModule
	public void processaDispensacaoDiluente(MpmPrescricaoMdto prescricaoMdto, 
			BigDecimal percSeNec, Date pmeData, Date pmeDthrFim, String nomeMicrocomputador, 
			Boolean movimentacao
			) throws ApplicationBusinessException{
		afaDispensacaoDiluenteON.processaDispensacaoDiluente(prescricaoMdto, percSeNec, pmeData, pmeDthrFim, nomeMicrocomputador, movimentacao);
	}

	@Override
	@BypassInactiveModule
	public AfaItemPreparoMdto obterItemPreparoMdtosPorChavePrimaria(Short seqp, Integer ptoSeq){
		return afaItemPreparoMdtosDAO.obterPorChavePrimaria(new AfaItemPreparoMdtoId(seqp, ptoSeq));
	}
	@Override
	public Long listarViasAdministracaoCount(String param) {
		return getAfaViaAdministracaoDAO().obterViaAdminstracaoSiglaouDescricaoCount(param);
	}

	@Override
	@BypassInactiveModule
	public List<ComponenteNPTVO> pesquisarHistoricoComponenteNPT(Integer seqComponente) {
		return afaComponenteNptJnDAO.pesquisarHistoricoComponenteNPT(seqComponente);
	}
	
	@Override
	@BypassInactiveModule
	public List<AfaGrupoComponNptJn> pesquisarHistoricoGrupoComponenteNPT(Short seqComponente) {
		return afaGrupoComponenteNptJnDAO.listarPorSeqGrupo(seqComponente);
	}
	
	@Override
	@BypassInactiveModule
	public List<ComponenteNPTVO> pesquisarComponentesNPT(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao,Integer firstResult, 
			Integer maxResult,String orderProperty, boolean asc) {
		return afaComponenteNptDAO.pesquisarComponentesNPT(medicamento,grupo,
				descricao,situacao,adulto,pediatria, ordem, identificacao,firstResult, maxResult, orderProperty, asc);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarComponentesNPTCount(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao) {
		return afaComponenteNptDAO.pesquisarComponentesNPTCount(medicamento,grupo,
				descricao,situacao,adulto,pediatria,
				ordem, identificacao);	
	}
	
	@Override
	@BypassInactiveModule
	public AghUnidadesFuncionais getUnidadeFuncionalAssociada(String computador)
			throws ApplicationBusinessException {
		return this.getEtiquetasON().carregarUnidadeFuncionalImpressao(
				computador);
	}
	
	@Override
	public List<AfaMedicamento> obterMedicamentosParaInclusaoLocalDispensacao(List<Integer> listaMateriais) {
		return afaMedicamentoDAO.obterMedicamentosParaInclusaoLocalDispensacao(listaMateriais);
	}
	
	@Override
	@BypassInactiveModule
	public List<MedicamentoVO> obterMedicamentosEnfermeiroObstetra(String strPesquisa,
			Boolean listaMedicamentos, DominioSituacaoMedicamento[] situacoes,
			Boolean somenteMdtoDeUsoAmbulatorial) {
		return this.getAfaMedicamentoDAO().obterMedicamentosEnfermeiroObstetra(strPesquisa,
				listaMedicamentos, situacoes, somenteMdtoDeUsoAmbulatorial);
	}

	@Override
	@BypassInactiveModule
	public List<VAfaDescrMdto> obterMedicamentosEnfermeiroObstetraReceitaVO(Object strPesquisa) {
		return this.getFarmaciaON().obterMedicamentosEnfermeiroObstetraReceitaVO(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public void verificarVinculoDiluenteExistente(AfaMedicamento medicamento, Boolean indDiluente, DominioSituacaoMedicamento situacao) throws ApplicationBusinessException {
		getCadastroDiluentesON().verificarVinculoDiluenteExistente(medicamento, indDiluente, situacao);
	}
	
	@Override
	@BypassInactiveModule
	public DiluentesVO buscarUsualPrescricaoPorMedicamento(Integer codigoMedicamento) {
		return getCadastroDiluentesON().popularBuscaUsualPrescricaoPorMedicamento(codigoMedicamento);
	}
	
	@Override
	@BypassInactiveModule
	public AfaVinculoDiluentes obterDiluente(Integer codigoDiluente) {
		return getCadastroDiluentesON().obterDiluente(codigoDiluente);
	}
	
	@Override
	@BypassInactiveModule
	public List<DiluentesVO> recuperaListaVinculoDiluente(String descricaoMedicamento , AfaMedicamento medicamento) {
		return this.getCadastroDiluentesON().recuperaListaVinculoDiluente(descricaoMedicamento, medicamento);
	}
	
	@Override
	@BypassInactiveModule
	public List<DiluentesVO> populaVoDiluentesVAfaDescrMdto(String descricaoMedicamento) {
		return this.getCadastroDiluentesON().populaVoDiluentesVAfaDescrMdto(descricaoMedicamento);
	}
	
	public void efetuarInclusaoDiluente(AfaVinculoDiluentes diluente, RapServidores servidorLogado, VAfaDescrMdto diluenteSelecionado, Integer codigoMedicamentoSelecionado) throws ApplicationBusinessException {
		this.getCadastroDiluentesON().persistirVinculoDiluentes(diluente, servidorLogado, diluenteSelecionado, codigoMedicamentoSelecionado);	
	}
	
	public void efetuarAlteracaoDiluente(AfaVinculoDiluentes diluente, RapServidores servidorLogado, VAfaDescrMdto diluenteSelecionado, Integer codigoMedicamentoSelecionado) throws ApplicationBusinessException {
		this.getCadastroDiluentesON().persistirVinculoDiluentes(diluente, servidorLogado, diluenteSelecionado, codigoMedicamentoSelecionado);
	}
	
	public void efetuarRemocao(AfaVinculoDiluentes diluente, RapServidores servidorLogado) throws ApplicationBusinessException{
	    this.getCadastroDiluentesON().excluirDiluente(diluente, servidorLogado);
		
	}
	
	@Override
	@BypassInactiveModule
	public List<CadastroDiluentesVO> recuperarListaPaginadaDiluentes(
			Integer firstResult, Integer maxResult,	String orderProperty,
			boolean asc, AfaMedicamento medicamento) {
		return getCadastroDiluentesON().recuperarListaPaginadaDiluentes(firstResult, maxResult, orderProperty, asc, medicamento);
	}

	public Long pesquisarDiluentesCount(AfaMedicamento medicamento) {
		return this.getCadastroDiluentesON().pesquisarDiluentesCount(medicamento);
	}
	
	@Override
	@BypassInactiveModule
	public List<CadastroDiluentesJnVO> pesquisarVinculoDiluentesJn(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigoMedicamento) {
		return this.getCadastroDiluentesON().pesquisarVinculoDiluentesJn(firstResult, maxResult, orderProperty, asc, codigoMedicamento);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarVinculoDiluentesJnCount(Integer codMedicamento) {
		return this.getCadastroDiluentesON().pesquisarVinculoDiluentesJnCount(codMedicamento);
	}	

}