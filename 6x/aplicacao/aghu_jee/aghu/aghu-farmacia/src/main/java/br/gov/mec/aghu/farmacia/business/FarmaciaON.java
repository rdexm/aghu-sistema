package br.gov.mec.aghu.farmacia.business;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.UnitarizacaoVO;
import br.gov.mec.aghu.farmacia.business.EtiquetasON.TipoCodigoBarrasMedicamento;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoMedicamentoMensagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemGrupoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMensagemMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdministracaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.MpmInformacaoPrescribentesDAO;
import br.gov.mec.aghu.farmacia.dao.VAfaDescrMdtoDAO;
import br.gov.mec.aghu.farmacia.vo.InformacoesPacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.MpmPrescricaoMedVO;
import br.gov.mec.aghu.farmacia.vo.PacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.farmacia.vo.ViaAdministracaoVO;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagem;
import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagemId;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamentoId;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaAuxiliarDAO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class FarmaciaON extends BaseBusiness {


@EJB
private FarmaciaRN farmaciaRN;

@EJB
private TipoVelocidadeAdministracaoRN tipoVelocidadeAdministracaoRN;

private static final Log LOG = LogFactory.getLog(FarmaciaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;

@Inject
private AfaViaAdministracaoDAO afaViaAdministracaoDAO;

@Inject
private AfaViaAdministracaoMedicamentoDAO afaViaAdministracaoMedicamentoDAO;

@Inject
private AfaGrupoMedicamentoMensagemDAO afaGrupoMedicamentoMensagemDAO;

@Inject
private AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO;

@Inject
private AfaMensagemMedicamentoDAO afaMensagemMedicamentoDAO;

@Inject
private AfaMedicamentoDAO afaMedicamentoDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private VAfaDescrMdtoDAO vAfaDescrMdtoDAO;

@Inject
private AfaGrupoMedicamentoDAO afaGrupoMedicamentoDAO;

@Inject
private AfaFormaDosagemDAO afaFormaDosagemDAO;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmInformacaoPrescribentesDAO mpmInformacaoPrescribentesDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@Inject
	private MpmPrescricaoMedicaAuxiliarDAO mpmPrescricaoMedicaDAO;

	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -626074647884372691L;
	
	private enum ValidacaoNulidadeExceptionCode implements BusinessExceptionCode {
		AGHU_IFP_ERRO_INFORME_PACIENTE,
		AGHU_IFP_INFO_PRESCRIBENTE_NAO_ENCONTRADA,
		ERRO_PRESCRICAO_MEDICA_NAO_POSSUI_ATENDIMENTO,
		;
	}

	private FarmaciaRN getFarmaciaRN() {
		return farmaciaRN;
	}

	private VAfaDescrMdtoDAO getVAfaDescrMdtoDAO() {
		return vAfaDescrMdtoDAO;
	}

	private AfaTipoVelocAdministracoesDAO getAfaTipoVelocAdministracoesDAO() {
		return afaTipoVelocAdministracoesDAO;
	}

	private AfaViaAdministracaoMedicamentoDAO getAfaViaAdministracaoMedicamentoDAO() {
		return afaViaAdministracaoMedicamentoDAO;
	}

	private AfaItemGrupoMedicamentoDAO getAfaItemGrupoMedicamentoDAO() {
		return afaItemGrupoMedicamentoDAO;
	}

	protected AfaMedicamentoDAO getAfaMedicamentoDAO() {
		return afaMedicamentoDAO;
	}

	protected AfaFormaDosagemDAO getAfaFormaDosagensDAO() {
		return afaFormaDosagemDAO;
	}

	protected AfaViaAdministracaoDAO getAfaViaAdministracaoDAO() {
		return afaViaAdministracaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}


	public List<VAfaDescrMdto> obtemListaDiluentes() {
		return this.getVAfaDescrMdtoDAO().obtemListaDiluentes();
	}
	
	public List<VAfaDescrMdto> obterListaDiluentes(String parametro) {
		return this.getVAfaDescrMdtoDAO().obterListaDiluentes(parametro);
	}

	public List<VAfaDescrMdto> obtemListaDiluentes(Integer matCodigo) {
		return this.getVAfaDescrMdtoDAO().obtemListaDiluentes(matCodigo);
	}

	public List<AfaTipoVelocAdministracoes> obtemListaTiposVelocidadeAdministracao() {
		return this.getAfaTipoVelocAdministracoesDAO()
		.obterListaTiposVelocidadeAdministracao();
	}
	
	public Boolean isViaAdministracaoBombaInfusao(AfaViaAdministracao viaAdministracao) {
		return viaAdministracao.getIndPermiteBi();
	}

	public AfaViaAdministracaoMedicamento obterAfaViaAdministracaoMedicamento(
			AfaViaAdministracaoMedicamentoId chave) {
		return this.getAfaViaAdministracaoMedicamentoDAO()
		.obterPorChavePrimaria(chave);
	}
	
	public List<VAfaDescrMdto> obterMedicamentosEnfermeiroObstetraReceitaVO(Object strPesquisa) {
		return this.getAfaMedicamentoDAO().obterMedicamentosEnfermeiroObstetraReceitaVO(strPesquisa);
	}
	
	public String obterInformacoesFarmacologicas(AfaMedicamento medicamento) {
		return this.getAfaItemGrupoMedicamentoDAO()
		.obterInformacoesFarmacologicas(medicamento);
	}
	
	public AfaMedicamento obterMedicamentoPorId(Integer medMatCodigo) {
		return this.getAfaMedicamentoDAO().obterPorChavePrimaria(medMatCodigo);
	}

	public AfaFormaDosagem buscarDosagenPadraoMedicamento(Integer medMatCodigo) {
		return this.getAfaFormaDosagensDAO().buscarDosagenPadraoMedicamento(
				medMatCodigo);
	}

	public List<AfaFormaDosagem> listarDosagensPadraoMedicamento(Integer medMatCodigo) {
		return this.getAfaFormaDosagensDAO().listaFormaDosagemMedicamento(medMatCodigo);
	}

	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa,
			List<Integer> medMatCodigo, Short seqUnidadeFuncional) {
		return this.getAfaViaAdministracaoDAO().listarViasMedicamento(
				strPesquisa, medMatCodigo, seqUnidadeFuncional);
	}

	public List<AfaViaAdministracao> listarTodasAsVias(String strPesq,
			Short unfSeq) {
		return this.getAfaViaAdministracaoDAO().listarTodasAsVias(strPesq,
				unfSeq);
	}

	public Long listarTodasAsViasCount(String strPesq, Short unfSeq) {
		return this.getAfaViaAdministracaoDAO().listarTodasAsViasCount(strPesq,
				unfSeq);
	}

	public Boolean verificarViaAssociadaAoMedicamento(Integer medMatCodigo,
			String sigla) {
		return this.getAfaViaAdministracaoMedicamentoDAO()
		.verificarViaAssociadaAoMedicamento(medMatCodigo, sigla);
	}

	public Boolean verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(
			Integer medMatCodigo, String sigla) {
		return this.getAfaViaAdministracaoMedicamentoDAO()
		.verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(
				medMatCodigo, sigla);
	}

	public Long listarViasMedicamentoCount(String strPesquisa,
			List<Integer> medMatCodigo, Short seqUnidadeFuncional) {
		return this.getAfaViaAdministracaoDAO().listarViasMedicamentoCount(
				strPesquisa, medMatCodigo, seqUnidadeFuncional);
	}

	public boolean isTipoVelocidadeAtiva(Short seq) throws ApplicationBusinessException {
		return getFarmaciaRN().isTipoVelocidadeAtiva(seq);
	}

	public List<MedicamentoVO> obterMedicamentosModeloBasicoVO(
			Object strPesquisa) {
		return this.getAfaMedicamentoDAO().obterMedicamentosModeloBasicoVO(
				strPesquisa);
	}

	public Long obterMedicamentosModeloBasicoVOCount(Object strPesquisa) {
		return this.getAfaMedicamentoDAO()
		.obterMedicamentosModeloBasicoVOCount(strPesquisa);
	}

	public List<AfaMedicamento> obterMedicamentos(Object strPesquisa,
			Boolean listaMedicamentos) {
		return this.getAfaMedicamentoDAO().obterMedicamentos(strPesquisa,
				listaMedicamentos);
	}

	public Integer obterMedicamentosCount(Object strPesquisa,
			Boolean listaMedicamentos) {
		return this.getAfaMedicamentoDAO().obterMedicamentosCount(strPesquisa,
				listaMedicamentos);
	}

	public List<VAfaDescrMdto> obterMedicamentosReceitaVO(Object strPesquisa) {
		return this.getAfaMedicamentoDAO().obterMedicamentosReceitaVO(
				strPesquisa);
	}

	public Long obterMedicamentosReceitaVOCount(Object strPesquisa) {
		return this.getAfaMedicamentoDAO().obterMedicamentosReceitaVOCount(
				strPesquisa);
	}

	public Long pesquisaAfaVelocidadesAdministracaoCount(Short filtroSeq,
			String filtroDescricao, BigDecimal filtroFatorConversaoMlH,
			Boolean filtroTipoUsualNpt, Boolean filtroTipoUsualSoroterapia,
			DominioSituacao filtroSituacao) {
		return getAfaTipoVelocAdministracoesDAO()
		.pesquisaVelocidadesAdministracaoCount(filtroSeq,
				filtroDescricao, filtroFatorConversaoMlH,
				filtroTipoUsualNpt, filtroTipoUsualSoroterapia,
				filtroSituacao);
	}

	public List<AfaTipoVelocAdministracoes> pesquisaAfaVelocidadesAdministracao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao,
			BigDecimal filtroFatorConversaoMlH, Boolean filtroTipoUsualNpt,
			Boolean filtroTipoUsualSoroterapia, DominioSituacao filtroSituacao) {
		return getAfaTipoVelocAdministracoesDAO()
		.pesquisaVelocidadesAdministracao(firstResult, maxResults,
				orderProperty, asc, filtroSeq, filtroDescricao,
				filtroFatorConversaoMlH, filtroTipoUsualNpt,
				filtroTipoUsualSoroterapia, filtroSituacao);
	}

	public AfaTipoVelocAdministracoes obterAfaTipoVelocAdministracoes(
			Short seqVelocidadeAdministracao) {
		return getAfaTipoVelocAdministracoesDAO().obterPorChavePrimaria(
				seqVelocidadeAdministracao);
	}

	public void removerAfaTipoVelocAdministracoes(Short seq) throws BaseException {
		this.getFarmaciaRN().removerAfaTipoVelocAdministracoes(seq);
	}

	public void persistirAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
	throws BaseException {
		tipoVelocidadeAdministracao.setDescricao(StringUtil.trim(
				tipoVelocidadeAdministracao.getDescricao()).toUpperCase());
		if (tipoVelocidadeAdministracao.getSeq() == null) {
			this.inserirAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao);
		} else {
			this
			.atualizarAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao);
		}
	}

	private void inserirAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
	throws ApplicationBusinessException {
		this.getTipoVelocidadeAdministracaoRN()
		.inserirAfaTipoVelocAdministracoes(tipoVelocidadeAdministracao);
	}

	private void atualizarAfaTipoVelocAdministracoes(
			AfaTipoVelocAdministracoes tipoVelocidadeAdministracao)
	throws ApplicationBusinessException {
		this.getTipoVelocidadeAdministracaoRN()
		.atualizarAfaTipoVelocAdministracoes(
				tipoVelocidadeAdministracao);
	}

	public AfaMensagemMedicamento obterAfaMensagemMedicamento(
			Integer seqMensagemMedicamento) {
		return this.getAfaMensagemMedicamentoDAO().obterPorChavePrimaria(
				seqMensagemMedicamento);
	}

	public void removerAfaMensagemMedicamento(Integer mensagemMedicamentoSeq) throws ApplicationBusinessException {
		AfaMensagemMedicamento mensagemMedicamento = getAfaMensagemMedicamentoDAO().obterPorChavePrimaria(mensagemMedicamentoSeq);
		if(mensagemMedicamento != null){
			this.getFarmaciaRN().removerAfaMensagemMedicamento(mensagemMedicamento);
		}
		else{
			throw new ApplicationBusinessException(FarmaciaExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
	}

	public Long pesquisaAfaMensagemMedicamentoCount(Integer filtroSeq,
			String filtroDescricao, Boolean filtroCoexistente,
			DominioSituacao filtroSituacao) {
		return this.getAfaMensagemMedicamentoDAO()
		.pesquisaMensagemMedicamentoCount(filtroSeq, filtroDescricao,
				filtroCoexistente, filtroSituacao);
	}

	public List<AfaMensagemMedicamento> pesquisaAfaMensagemMedicamento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer filtroSeq, String filtroDescricao,
			Boolean filtroCoexistente, DominioSituacao filtroSituacao) {
		return this.getAfaMensagemMedicamentoDAO().pesquisaMensagemMedicamento(
				firstResult, maxResults, orderProperty, asc, filtroSeq,
				filtroDescricao, filtroCoexistente, filtroSituacao);
	}

	public void persistirAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
	throws BaseException {
		mensagemMedicamento.setDescricao(mensagemMedicamento.getDescricao()
				.toUpperCase());
		if (mensagemMedicamento.getSeq() == null) {
			this.inserirAfaMensagemMedicamento(mensagemMedicamento);
		} else {
			this.atualizarAfaMensagemMedicamento(mensagemMedicamento);
		}
	}

	public void inserirAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
	throws BaseException {
		this.getFarmaciaRN().inserirAfaMensagemMedicamento(mensagemMedicamento);

		List<AfaGrupoMedicamentoMensagem> gruposMedicamentoMensagem = mensagemMedicamento
		.getGruposMedicamentosMensagem();
		if (gruposMedicamentoMensagem != null
				&& !gruposMedicamentoMensagem.isEmpty()) {
			for (AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem : gruposMedicamentoMensagem) {
				this.persistirAfaGrupoMedicamentoMensagem(grupoMedicamentoMensagem);
			}
		}
	}

	public void atualizarAfaMensagemMedicamento(
			AfaMensagemMedicamento mensagemMedicamento)
	throws BaseException {
		AfaGrupoMedicamentoMensagemDAO afaGrupoMedicamentoMensagemDAO = this
		.getAfaGrupoMedicamentoMensagemDAO();
		List<AfaGrupoMedicamentoMensagem> gruposMedicamentoMensagem = mensagemMedicamento
		.getGruposMedicamentosMensagem();

		List<AfaGrupoMedicamentoMensagem> listaGrupoMedicamentoMensagem = afaGrupoMedicamentoMensagemDAO
		.pesquisarPorSeqMensagemMedicamento(mensagemMedicamento
				.getSeq());
		if (listaGrupoMedicamentoMensagem != null
				&& !listaGrupoMedicamentoMensagem.isEmpty()) {
			for (AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem : listaGrupoMedicamentoMensagem) {
				if (gruposMedicamentoMensagem != null && !gruposMedicamentoMensagem
						.contains(grupoMedicamentoMensagem)) {
					afaGrupoMedicamentoMensagemDAO
					.remover(grupoMedicamentoMensagem);
					afaGrupoMedicamentoMensagemDAO.flush();
					afaGrupoMedicamentoMensagemDAO
					.desatachar(grupoMedicamentoMensagem);
				}
			}
		}

		if (gruposMedicamentoMensagem != null
				&& !gruposMedicamentoMensagem.isEmpty()) {
			for (AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem : gruposMedicamentoMensagem) {
				this.persistirAfaGrupoMedicamentoMensagem(grupoMedicamentoMensagem);
			}
		}

		this.getFarmaciaRN().atualizarAfaMensagemMedicamento(
				mensagemMedicamento);
	}

	public void persistirAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
	throws BaseException  {
		if (grupoMedicamentoMensagem.getId() == null) {
			Integer memSeq = grupoMedicamentoMensagem.getMensagemMedicamento()
			.getSeq();
			Short gmdSeq = grupoMedicamentoMensagem.getGrupoMedicamento()
			.getSeq();

			AfaGrupoMedicamentoMensagemId id = new AfaGrupoMedicamentoMensagemId(
					memSeq, gmdSeq);
			grupoMedicamentoMensagem.setId(id);
			try {
				this.inserirAfaGrupoMedicamentoMensagem(grupoMedicamentoMensagem);
			} catch(BaseException  e) {
				grupoMedicamentoMensagem.setId(null);
				throw new ApplicationBusinessException(e);
			}
		} else {
			this.atualizarAfaGrupoMedicamentoMensagem(grupoMedicamentoMensagem);
		}
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(Object parametroPesquisa) {
		List<AghUnidadesFuncionais.Fields> atributosOrder = new ArrayList<AghUnidadesFuncionais.Fields>();
		atributosOrder.add(AghUnidadesFuncionais.Fields.ANDAR );
		atributosOrder.add(AghUnidadesFuncionais.Fields.ALA );
		atributosOrder.add(AghUnidadesFuncionais.Fields.DESCRICAO );
		List<AghUnidadesFuncionais> result = getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(parametroPesquisa.toString(), 
				DominioSituacao.A, atributosOrder, ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA, ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA, ConstanteAghCaractUnidFuncionais.CO, ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO);

		return result;
	}	

	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
			String strPesquisa) {

		return getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(
				strPesquisa, DominioSituacao.A, 
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA, ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, 
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA, ConstanteAghCaractUnidFuncionais.CO, 
				ConstanteAghCaractUnidFuncionais.ZONA_AMBULATORIO);
	}

	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(Object strPesquisa) {

		List<AghUnidadesFuncionais.Fields> fieldsOrder = Arrays.asList(
				AghUnidadesFuncionais.Fields.ANDAR,
				AghUnidadesFuncionais.Fields.ALA,
				AghUnidadesFuncionais.Fields.DESCRICAO);

		return getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
				strPesquisa, DominioSituacao.A, Boolean.TRUE,Boolean.TRUE, Boolean.TRUE, 
				fieldsOrder, ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
	}

	public Long listarFarmaciasAtivasByPesquisaCount(Object strPesquisa) {

		return getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				strPesquisa, DominioSituacao.A, Boolean.TRUE,Boolean.TRUE, Boolean.TRUE,
				ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
	}

	private void inserirAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
	throws ApplicationBusinessException {
		this.getFarmaciaRN().inserirAfaGrupoMedicamentoMensagem(
				grupoMedicamentoMensagem);
	}

	private void atualizarAfaGrupoMedicamentoMensagem(
			AfaGrupoMedicamentoMensagem grupoMedicamentoMensagem)
	throws BaseException  {
		this.getFarmaciaRN().atualizarAfaGrupoMedicamentoMensagem(
				grupoMedicamentoMensagem);
	}

	public List<AfaGrupoMedicamento> pesquisaGruposMedicamentos(Object filtro) {
		return this.getAfaGrupoMedicamentoDAO().pesquisaGruposMedicamentos(
				filtro);
	}

	public Long pesquisaGruposMedicamentosCount(String filtro) {
		return this.getAfaGrupoMedicamentoDAO()
		.pesquisaGruposMedicamentosCount(filtro);
	}

	public AfaGrupoMedicamento obterAfaGrupoMedicamento(
			Short seqGrupoMedicamento) {
		return this.getAfaGrupoMedicamentoDAO().obterPorChavePrimaria(
				seqGrupoMedicamento);
	}

	public void removerAfaGrupoMedicamento(Short seqAfaGrupoMedicamento)
	throws BaseException {
		AfaGrupoMedicamento grupoMedicamento = getAfaGrupoMedicamentoDAO().obterPorChavePrimaria(seqAfaGrupoMedicamento);
		
		if(grupoMedicamento != null){
			if (grupoMedicamento.getGruposMedicamentosMensagem()!=null && !grupoMedicamento.getGruposMedicamentosMensagem().isEmpty()){
				throw new ApplicationBusinessException(FarmaciaExceptionCode.OFG_00005, "Grupo Medicamento", "Grupo Medicamentos Mensagens");
			}
			
			this.getFarmaciaRN().removerAfaGrupoMedicamento(grupoMedicamento);
		}
		else{
			throw new ApplicationBusinessException (FarmaciaExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
	}

	public List<AfaGrupoMedicamento> pesquisaAfaGrupoMedicamento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao,
			Boolean filtroMedicamentoMesmoSal, Boolean filtroUsoFichaAnestesia,
			DominioSituacao filtroSituacao) {
		return this.getAfaGrupoMedicamentoDAO().pesquisaAfaGrupoMedicamento(
				firstResult, maxResults, orderProperty, asc, filtroSeq,
				filtroDescricao, filtroMedicamentoMesmoSal,
				filtroUsoFichaAnestesia, filtroSituacao);
	}

	public Long pesquisaAfaGrupoMedicamentoCount(Short filtroSeq,
			String filtroDescricao, Boolean filtroMedicamentoMesmoSal,
			Boolean filtroUsoFichaAnestesia, DominioSituacao filtroSituacao) {
		return this.getAfaGrupoMedicamentoDAO()
		.pesquisaAfaGrupoMedicamentoCount(filtroSeq, filtroDescricao,
				filtroMedicamentoMesmoSal, filtroUsoFichaAnestesia,
				filtroSituacao);
	}

	public void persistirAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws BaseException {
		grupoMedicamento.setDescricao(StringUtil.trim(
				grupoMedicamento.getDescricao()).toUpperCase());
		if (grupoMedicamento.getSeq() == null) {
			this.inserirAfaGrupoMedicamento(grupoMedicamento);
		} else {
			this.atualizarAfaGrupoMedicamento(grupoMedicamento);
		}
	}

	private void inserirAfaGrupoMedicamento(AfaGrupoMedicamento grupoMedicamento)
	throws BaseException {
		this.getFarmaciaRN().inserirAfaGrupoMedicamento(grupoMedicamento);

		List<AfaItemGrupoMedicamento> itensGruposMedicamento = grupoMedicamento
		.getItensGruposMedicamento();
		if (itensGruposMedicamento != null && !itensGruposMedicamento.isEmpty()) {
			for (AfaItemGrupoMedicamento itemGrupoMedicamento : itensGruposMedicamento) {
				this.persistirAfaItemGrupoMedicamento(itemGrupoMedicamento);
			}
		}
	}

	private void atualizarAfaGrupoMedicamento(
			AfaGrupoMedicamento grupoMedicamento) throws BaseException  {
		AfaItemGrupoMedicamentoDAO afaItemGrupoMedicamentoDAO = this
		.getAfaItemGrupoMedicamentoDAO();
		List<AfaItemGrupoMedicamento> itensGrupoMedicamento = grupoMedicamento
		.getItensGruposMedicamento();

		List<AfaItemGrupoMedicamento> listaItemGrupoMedicamento = afaItemGrupoMedicamentoDAO
		.pesquisarPorSeqGrupoMedicamento(grupoMedicamento.getSeq());
		if (listaItemGrupoMedicamento != null
				&& !listaItemGrupoMedicamento.isEmpty()) {
			for (AfaItemGrupoMedicamento itemGrupoMedicamento : listaItemGrupoMedicamento) {
				if (!itensGrupoMedicamento.contains(itemGrupoMedicamento)) {
					afaItemGrupoMedicamentoDAO.remover(itemGrupoMedicamento);
					afaItemGrupoMedicamentoDAO.flush();
					afaItemGrupoMedicamentoDAO.desatachar(itemGrupoMedicamento);
				}
			}
		}

		if (itensGrupoMedicamento != null && !itensGrupoMedicamento.isEmpty()) {
			for (AfaItemGrupoMedicamento itemGrupoMedicamento : itensGrupoMedicamento) {
				this.persistirAfaItemGrupoMedicamento(itemGrupoMedicamento);
			}
		}

		this.getFarmaciaRN().atualizarAfaGrupoMedicamento(grupoMedicamento);
	}

	public void persistirAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
	throws BaseException {
		if (itemGrupoMedicamento.getId() == null) {
			Integer matCodigo = itemGrupoMedicamento.getMedicamento()
			.getMatCodigo();
			Short gmdSeq = itemGrupoMedicamento.getGrupoMedicamento().getSeq();

			AfaItemGrupoMedicamentoId id = new AfaItemGrupoMedicamentoId(
					matCodigo, gmdSeq);
			itemGrupoMedicamento.setId(id);
			try {
				this.inserirAfaItemGrupoMedicamento(itemGrupoMedicamento);
			} catch (ApplicationBusinessException e) {
				itemGrupoMedicamento.setId(null);
				throw e;
			}
		} else {
			this.atualizarAfaItemGrupoMedicamento(itemGrupoMedicamento);
		}
	}

	private void inserirAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
	throws BaseException {
		this.getFarmaciaRN().inserirAfaItemGrupoMedicamento(
				itemGrupoMedicamento);
	}

	private void atualizarAfaItemGrupoMedicamento(
			AfaItemGrupoMedicamento itemGrupoMedicamento)
	throws BaseException {
		this.getFarmaciaRN().atualizarAfaItemGrupoMedicamento(
				itemGrupoMedicamento);
	}

	protected TipoVelocidadeAdministracaoRN getTipoVelocidadeAdministracaoRN() {
		return tipoVelocidadeAdministracaoRN;
	}

	protected AfaMensagemMedicamentoDAO getAfaMensagemMedicamentoDAO() {
		return afaMensagemMedicamentoDAO;
	}

	protected AfaGrupoMedicamentoMensagemDAO getAfaGrupoMedicamentoMensagemDAO() {
		return afaGrupoMedicamentoMensagemDAO;
	}

	protected AfaGrupoMedicamentoDAO getAfaGrupoMedicamentoDAO() {
		return afaGrupoMedicamentoDAO;
	}


	/**
	 * Este método é responsável por gerar o arquivo de interfaceamento para 
	 * a máquina unitarizadora de medicamentos da marca Grifols.
	 * O resultado final do método é a geração de um arquivo do tipo CSV, 
	 * especificado no parâmetro de sistema P_AGHU_CAMINHO_ARQUIVO_GRIFOLS,
	 * que será lido pelo equipamento.
	 * 
	 * Um exemplo de saída deste arquivo seria:
	 * AAS 100mg;COMP;abc123;10/10/2010;Grifols;012345678900001;HCPA
	 * 
	 * @param list Lista de medicamentos a ser unitarizado pela Grifols
	 * @throws ApplicationBusinessException
	 */
	public void gerarCSVInterfaceamentoImpressoraGriffols(UnitarizacaoVO unitarizacaoVO, Long quantidade) throws ApplicationBusinessException{

		try {

			File arq = obterCaminhoDiretorio();

			arq.createNewFile();
			FileWriter fileWriter = new FileWriter(arq, false);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			Integer numeroInicial = unitarizacaoVO.getNroInicial();
			Integer quantidadeEtiquetas = 0;
			AghParametros nomeHuEti = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_NOME_HU_ETIQUETA);
			TipoCodigoBarrasMedicamento tipoCodigoBarras = TipoCodigoBarrasMedicamento.obterPorParametro(getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO));
			
			while (quantidadeEtiquetas < quantidade) {
				//Nome
				printWriter.print(unitarizacaoVO.getNomeMed() + ";");
				//Dosagem
				printWriter.print(unitarizacaoVO.getConcentracao() + ";");
				//Validade
				printWriter.print("VAL:"+new SimpleDateFormat("MM/yyyy").format(unitarizacaoVO.getDtValidade()) + ";");
				//Nro Lote
				printWriter.print("LOTE:"+unitarizacaoVO.getLotCodigo() + ";");
				//Laboratório
				printWriter.print("LAB:"+unitarizacaoVO.getLaboratorio() + ";");
				//Hospital
				printWriter.print(nomeHuEti.getVlrTexto()+ ";");
				
				//Código de Barras
				printWriter.println(tipoCodigoBarras.geraCodigoBarras(unitarizacaoVO.getLotSeq(), numeroInicial) + ";");

				numeroInicial = numeroInicial + 1;
				quantidadeEtiquetas = quantidadeEtiquetas + 1;
				
			}
			
			printWriter.flush();
			printWriter.close();

		} catch (IOException e) {
			logError(e);
			throw new ApplicationBusinessException(FarmaciaExceptionCode.ERRO_GERACAO_ARQUIVO);
		}catch (Exception e) {
			logError(e);
			throw new ApplicationBusinessException(FarmaciaExceptionCode.ERRO_GERACAO_ARQUIVO);
		}

	}
	
	private File obterCaminhoDiretorio() throws ApplicationBusinessException {

		AghParametros caminhoArquivo = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_AGHU_CAMINHO_SAIDA_ARQUIVO_UNITARIZADORA);			
		if(caminhoArquivo== null || caminhoArquivo.getVlrTexto() == null || caminhoArquivo.getVlrTexto().isEmpty()){
			throw new ApplicationBusinessException(FarmaciaExceptionCode.MENSAGEM_ERRO_DIRETORIO_INEXISTENTE);
		}

		File arq = new File(caminhoArquivo.getVlrTexto());
		return arq;


	}
	
	/**
	 * Interfaceamento Impressora Grifols para o Hospital de Juiz de Fora.
	 * 
	 * @param unitarizacaoVO
	 * @param quantidade
	 */
	public void gerarCSVInterfaceamentoImpressoraHujf(UnitarizacaoVO unitarizacaoVO, Long quantidade) {

		try {

			File arq = obterCaminhoDiretorio();

			arq.createNewFile();
			FileWriter fileWriter = new FileWriter(arq, false);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			Integer numeroInicial = unitarizacaoVO.getNroInicial();

			//quantidade
			printWriter.print(quantidade + ";");
			//Nome
			printWriter.print(unitarizacaoVO.getNomeMed() + ";");
			//Dosagem
			printWriter.print(unitarizacaoVO.getConcentracao() + ";");
			//Apresentação
			printWriter.print(unitarizacaoVO.getSiglaApresentacaoMedicamento() + ";");
			//Laboratório
			printWriter.print(unitarizacaoVO.getLaboratorio() + ";");
			//Validade
			printWriter.print(new SimpleDateFormat("dd/MM/yyyy").format(unitarizacaoVO.getDtValidade()) + ";");
			//Máquina
			printWriter.print("Opus" + ";");
			//Código de Barras
			TipoCodigoBarrasMedicamento tipoCodigoBarras = TipoCodigoBarrasMedicamento.obterPorParametro(getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO));
			printWriter.print(tipoCodigoBarras.geraCodigoBarras(unitarizacaoVO.getLotSeq(), numeroInicial) + ";");

			//Nro Lote
			printWriter.print(unitarizacaoVO.getLotCodigo() + ";");
			//Hospital
			AghParametros nomeHuEti = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_NOME_HU_ETIQUETA);
			printWriter.println(nomeHuEti.getVlrTexto()+ ";");

			printWriter.flush();
			printWriter.close();

		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
			/*Gerar log de erro e seguir o fluxo de geração de etiquetas.*/
			//throw new AGHUNegocioExceptionSemRollback(ImprimirEtiquetasUnitarizacaoONExceptionCode.ERRO_GERACAO_ARQUIVO);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
		} 

	}
	
	public void validarEnvioInformacoesPrescribentes(PacienteAgendamentoPrescribenteVO paciente) throws ApplicationBusinessException{
		if(paciente == null){
			throw new ApplicationBusinessException(ValidacaoNulidadeExceptionCode.AGHU_IFP_ERRO_INFORME_PACIENTE);
		}
	}
	
	public void validarNulidadeInformacaoPrescribente(InformacoesPacienteAgendamentoPrescribenteVO informacoesPacienteAgendamentoPrescribenteVO) throws ApplicationBusinessException{
		if(informacoesPacienteAgendamentoPrescribenteVO == null){
			throw new ApplicationBusinessException(ValidacaoNulidadeExceptionCode.AGHU_IFP_INFO_PRESCRIBENTE_NAO_ENCONTRADA);
		}
	}
	
	public void validarRetornoPaciente(PacienteAgendamentoPrescribenteVO prescribenteVO) throws ApplicationBusinessException{
		if(prescribenteVO == null || prescribenteVO.getSeq() == null){
			throw new ApplicationBusinessException(ValidacaoNulidadeExceptionCode.ERRO_PRESCRICAO_MEDICA_NAO_POSSUI_ATENDIMENTO);
		}
	}
	
	public String gravarMpmInformacaoPrescribentes(InformacoesPacienteAgendamentoPrescribenteVO infPrescribenteVO, 
			MpmPrescricaoMedVO mpmPrescricaoMedica, PacienteAgendamentoPrescribenteVO prescribenteVO, String informacaoPrescribente, UnidadeFuncionalVO unidadeFuncional) throws ApplicationBusinessException{
		
		if(infPrescribenteVO != null && infPrescribenteVO.getIfpSeq() != null){
			MpmInformacaoPrescribente mpmInformacaoPrescribenteNew = this.prescricaoMedicaFacade.obterMpmInformacaoPrescribentePorChavePrimaria(infPrescribenteVO.getIfpSeq());
			MpmInformacaoPrescribente mpmInformacaoPrescribenteOld = new MpmInformacaoPrescribente();
			try {
				mpmInformacaoPrescribenteOld = (MpmInformacaoPrescribente) mpmInformacaoPrescribenteNew.clone();
			} catch (CloneNotSupportedException e) {
				LOG.error(e.getMessage(),e);
			}
			mpmInformacaoPrescribenteNew.setDescricao(informacaoPrescribente);
			prescricaoMedicaFacade.validarAlteracaoMpmInformacaoPrescribentes(mpmInformacaoPrescribenteNew, mpmInformacaoPrescribenteOld);
			mpmInformacaoPrescribentesDAO.atualizar(mpmInformacaoPrescribenteNew);
			return getResourceBundleValue("AGHU_IFP_INFO_PRESCRIBENTE_ALTERADA_SUCESSO");
			
		}else{
			MpmInformacaoPrescribente mpmInformacaoPrescribente = new MpmInformacaoPrescribente();
			mpmInformacaoPrescribente.setAtdSeq(prescribenteVO.getSeq());
			if(mpmPrescricaoMedica != null){
				mpmInformacaoPrescribente.setPmeSeq(mpmPrescricaoMedica.getSeq());
				mpmInformacaoPrescribente.setPmeAtdSeq(mpmPrescricaoMedica.getAtdSeq());
				MpmPrescricaoMedica prescricaoMedica = mpmPrescricaoMedicaDAO.obterPrescricaoMedicaPorId(
						mpmPrescricaoMedica.getSeq(), mpmPrescricaoMedica.getAtdSeq());
				mpmInformacaoPrescribente.setPrescricaoMedica(prescricaoMedica);
			}
			mpmInformacaoPrescribente.setDescricao(informacaoPrescribente);
			mpmInformacaoPrescribente.setUnfSeq(unidadeFuncional.getSeq());
			mpmInformacaoPrescribente.setIndInfVerificada(Boolean.FALSE);
			
			AghUnidadesFuncionais aghUnidadesFuncionais = aghUnidadesFuncionaisDAO.obterPorUnfSeq(unidadeFuncional.getSeq());
			mpmInformacaoPrescribente.setUnidadeFuncional(aghUnidadesFuncionais);

			
			AghAtendimentos atendimento = aghAtendimentoDAO.obterAtendimentoPeloSeq(prescribenteVO.getSeq());
			mpmInformacaoPrescribente.setAtendimento(atendimento);
			
			prescricaoMedicaFacade.validarInsercaoMpmInformacaoPrescribentes(mpmInformacaoPrescribente);
			mpmInformacaoPrescribentesDAO.persistir(mpmInformacaoPrescribente);
						
			return getResourceBundleValue("AGHU_IFP_INFO_PRESCRIBENTE_ENVIADA_SUCESSO");
		}
	}
	
	public Boolean validacaoData(String descricao){
		
		int dia = Integer.valueOf(descricao.substring(0, 2));
		int mes = Integer.valueOf(descricao.substring(3, 5));
		
		if(mes >= 1 && mes <= 12){
			if((mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8
					|| mes == 10 || mes == 12) && (dia >= 1 && dia <= 31)){
				return Boolean.TRUE;
			}else if((mes == 4 || mes == 6 || mes == 9 || mes == 11) 
					&& (dia >= 1 && dia <= 30)){
					return Boolean.TRUE;
			}else if(mes == 2 && (dia >= 1 && dia <=29)){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public List<ViaAdministracaoVO> pesquisarViaAdminstracaoSiglaouDescricao(String param) {
		List<ViaAdministracaoVO> result = new ArrayList<ViaAdministracaoVO>(); 
		List<AfaViaAdministracao> lista = getAfaViaAdministracaoDAO().obterViaAdminstracaoSiglaouDescricao(param);		
		if(lista == null || lista.isEmpty()) {
			return result;
		}
		for(AfaViaAdministracao item: lista) {
			result.add(new ViaAdministracaoVO(item.getSigla(), item.getDescricao()));
		}
		return result;
	}
}