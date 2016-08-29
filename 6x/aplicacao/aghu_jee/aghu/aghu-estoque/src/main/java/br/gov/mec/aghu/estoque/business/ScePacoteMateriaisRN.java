package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceItemPacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.ScePacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceProducaoInternaDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMaterialRetornosDAO;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceItemPacoteMateriais;
import br.gov.mec.aghu.model.SceItemPacoteMateriaisId;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScePacoteMateriaisRN extends BaseBusiness{

@EJB
private ManterTransferenciaMaterialRN manterTransferenciaMaterialRN;
@EJB
private ManterRequisicaoMaterialRN manterRequisicaoMaterialRN;

private static final Log LOG = LogFactory.getLog(ScePacoteMateriaisRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemPacoteMateriaisDAO sceItemPacoteMateriaisDAO;

@Inject
private SceProducaoInternaDAO sceProducaoInternaDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private SceReqMaterialRetornosDAO sceReqMaterialRetornosDAO;

@Inject
private ScePacoteMateriaisDAO scePacoteMateriaisDAO;

@EJB
private ICentroCustoFacade centroCustoFacade;

	
	private static final long serialVersionUID = -403473266209369089L;

	public enum ManterPacoteMateriaisRNExceptionCode implements BusinessExceptionCode {

		SCE_00394, SCE_00400, SCE_00393, SCE_00392, SCE_00298,
		SCE_00292, SCE_00175,
		ERRO_CODIGO_MATERIAL_JA_EXISTENTE, 
		ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_PROPRIETARIO_OBRIGATORIO,
		ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_APLICACAO_OBRIGATORIO,
		ERRO_PACOTE_MATERIAIS_ALMOXARIFADO_OBRIGATORIO,
		ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_INATIVO,
		ERRO_PACOTE_MATERIAIS_MATERIAL_OBRIGATORIO,
		ERRO_PACOTE_MATERIAIS_SITUACAO_OBRIGATORIA,
		ERRO_DEPENDENTES_TRANSFERENCIA,
		ERRO_DEPENDENTES_REQUISICAO,
		ERRO_DEPENDENTES_REQUISICAO_MATERIAL_RETORNOS,
		ERRO_DEPENDENTES_PRODUCAO_INTERNAS;

	}
	
	protected ScePacoteMateriaisDAO getScePacoteMateriaisDAO() {
		return scePacoteMateriaisDAO;
	}
	
	protected SceItemPacoteMateriaisDAO getSceItemPacoteMateriaisDAO() {
		return sceItemPacoteMateriaisDAO;
	}
	
	protected SceReqMaterialRetornosDAO getSceReqMaterialRetornosDAO(){
		return sceReqMaterialRetornosDAO;
	}
	
	protected SceProducaoInternaDAO getSceProducaoInternaDAO() {
		return sceProducaoInternaDAO;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}
	
	protected ManterTransferenciaMaterialRN getManterTransferenciaMaterialRN(){
		return manterTransferenciaMaterialRN;
	}
	
	protected ManterRequisicaoMaterialRN getManterRequisicaoMaterialRN(){
		return manterRequisicaoMaterialRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}	
	
	/**
	 * Valida se campos obrigatórios do pacote de material foram informados
	 * @param erros
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param seqAlmoxarifado
	 * @param numeroPacote
	 * @param situacao
	 * @throws BaseListException
	 */
	public void validarDadosObrigatoriosPacoteMaterial(BaseListException erros, Integer codigoCentroCustoProprietario, 
			Integer codigoCentroCustoAplicacao, Short seqAlmoxarifado, DominioSituacao situacao) {
		
		if(codigoCentroCustoProprietario == null) {
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_PROPRIETARIO_OBRIGATORIO, new Object[]{}));
		}
		if(codigoCentroCustoAplicacao == null) {
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_APLICACAO_OBRIGATORIO, new Object[]{}));
		}
		if(seqAlmoxarifado == null) {
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_ALMOXARIFADO_OBRIGATORIO, new Object[]{}));
		}
		if(situacao == null){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_SITUACAO_OBRIGATORIA, new Object[]{}));
		}
		
	}
	
	
	
	/**
	 * RN3: Verifica a presença do ítem a ser inserido na lista, no pacote de materiais
	 * 
	 * @param erros
	 * @param idItemPacoteMateriais
	 */
	private void verificarExistenciaItemPacoteMaterial(BaseListException erros, SceItemPacoteMateriaisId idItemPacoteMateriais){
		final Long VALOR_CONTROLA_EXISTENCIA_ITEM = 1l;
		Long quantidadeItens = getSceItemPacoteMateriaisDAO().obterQuantidadeItensPacoteMateriaisPorId(idItemPacoteMateriais);
		if(quantidadeItens.equals(VALOR_CONTROLA_EXISTENCIA_ITEM)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.SCE_00175));
		}
	}
	
	/**
	 * RN5: Valida a inclusão de pelo menos um item na lista
	 * 
	 * @ORADB : Procedure SCEK_PMT_RN.RN_PMTP_VER_FILHO
	 * RN5
	 * @param erros,
	 * @param quantidadeItensAdicionados
	 * 
	 */
	private void verificarAdicaoItemPacoteMateriais(BaseListException erros, Integer quantidadeItensAdicionados){
		// verifica se ao menos 1 item foi adicionado
		if(quantidadeItensAdicionados == 0) {
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.SCE_00392, new Object[]{}));	
		}
	}
	
	/**
	 * @ORADB: SCEK_PMT_RN.RN_SCEP_VER_CCT_ATIV 
	 * Verifica se o centro de custo informado está inativo.
	 * 
	 * @param codigoCentroCusto
	 * @return
	 */
	private boolean isCentroCustoInativo(Integer codigoCentroCusto) {
		FccCentroCustos centroCusto = getCentroCustoFacade().obterFccCentroCustos(codigoCentroCusto);
		if(centroCusto != null) {
			if(DominioSituacao.I.equals(centroCusto.getIndSituacao())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	
	/**
	 * Verifica se o almoxarifado está ativo 
	 * @ORADB: procedure RN_SCEP_VER_ALM_ATIV
	 * RN8
	 * @param erros
	 * @param seqAlmoxarifado
	 */
	private void verificarAlmoxarifadoAtivo(BaseListException erros, Short seqAlmoxarifado){
		if(!getManterTransferenciaMaterialRN().isAlmoxarifadoValido(seqAlmoxarifado)){
				erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.SCE_00298, new Object[]{}));	
		}
	}
	
	/**
	 * @ORADB: procedure SCEK_PMT_RN.RN_SCEP_VER_CCT_ATIV 
	 * Verifica se os centros de custo estão ativos
	 * RN9 e NR10
	 * @param erros
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 */
	public void verificarCentrosCustoAtivos(BaseListException erros, Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao){
		
		// centro custo proprietario
		if(codigoCentroCustoProprietario != null) {
			if(isCentroCustoInativo(codigoCentroCustoProprietario)) {					
				erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_INATIVO, codigoCentroCustoProprietario));
			}
		}
		// centro custo aplicacao
		if(codigoCentroCustoAplicacao != null) {
			if(isCentroCustoInativo(codigoCentroCustoAplicacao)) {					
				erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_CENTRO_CUSTO_INATIVO, codigoCentroCustoAplicacao));
			}
		}
	}	
	
	
	
	/**
	 * Realiza validações antes da inserção do material na lista de ítens
	 * 
	 * @param itensPacote
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @param seqEstoque
	 * @param quantidade
	 * @param codigoMaterial
	 * @throws BaseListException
	 * @throws ApplicationBusinessException
	 */
	public void preInsercaoItensPacote(List<ItemPacoteMateriaisVO> itensPacote, Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, 
			Integer numeroPacote, Integer seqEstoque, Integer quantidade, Integer codigoMaterial, Short seqAlmoxarifadoPai, Short seqAlmoxarifadoFilho, DominioSituacao situacaoAlmoxarifadoPai) throws BaseListException, ApplicationBusinessException{
		
		BaseListException erros = new BaseListException();
		
		SceItemPacoteMateriaisId idItemPacoteMateriais = new SceItemPacoteMateriaisId();
		idItemPacoteMateriais.setCodigoCentroCustoProprietarioPacoteMateriais(codigoCentroCustoProprietario);
		idItemPacoteMateriais.setCodigoCentroCustoAplicacaoPacoteMateriais(codigoCentroCustoAplicacao);
		idItemPacoteMateriais.setNumeroPacoteMateriais(numeroPacote);
		idItemPacoteMateriais.setSeqEstoque(seqEstoque);
		//RN3
		verificarExistenciaItemPacoteMaterial(erros, idItemPacoteMateriais);
		
		verificarSeCodigoMaterialExiste(erros, itensPacote, codigoMaterial);
		verificarMaterialObrigatorio(erros, codigoMaterial);
		//RN14 e 15
		verificarAlmoxarifadosItemEPacoteMateriais(erros, seqAlmoxarifadoPai, seqAlmoxarifadoFilho, situacaoAlmoxarifadoPai);
		
		if(erros.hasException()){
			throw erros;
		}
	}
	
	/**
	 * Verifica se já existe na lista um material com o mesmo código
	 * @param itens
	 * @param codigoMaterial
	 * @return
	 */
	public void verificarSeCodigoMaterialExiste(BaseListException erros, List<ItemPacoteMateriaisVO> itens, Integer codigoMaterial) {
		List<Integer> codigosListaItens = new ArrayList<Integer>();
		for(ItemPacoteMateriaisVO item : itens) {
			codigosListaItens.add(item.getCodigoMaterial());
		}
		if(codigosListaItens.contains(codigoMaterial)) {
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_CODIGO_MATERIAL_JA_EXISTENTE));
		}
	}
	
	
	/**
	 * Verifica se o material foi definido para inserção na lista
	 * @param erros
	 * @param codigoMaterial
	 */
	private void verificarMaterialObrigatorio(BaseListException erros, Integer codigoMaterial) {
		if(codigoMaterial==null){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_PACOTE_MATERIAIS_MATERIAL_OBRIGATORIO));
		}
	}
	
	/**
	 * RN14 e 15: Verifica se os almoxarifados do Pacote e do ítem são os mesmos
	 * 
	 * @ORADB: SCEK_PMT_RN.RN_IPMP_VER_ALM_EAL
	 * @param erros
	 * @param seqAlmoxarifadoPai
	 * @param seqAlmoxarifadoFilho
	 * @param situacaoAlmoxarifadoPai
	 */
	public void verificarAlmoxarifadosItemEPacoteMateriais(BaseListException erros, Short seqAlmoxarifadoPai, Short seqAlmoxarifadoFilho, DominioSituacao situacaoAlmoxarifadoPai){
		if(!seqAlmoxarifadoPai.equals(seqAlmoxarifadoFilho)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.SCE_00393));
		}
		if(DominioSituacao.I.equals(situacaoAlmoxarifadoPai)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.SCE_00292));
		}
	}


	/**
	 * @ORADB: Procedure SCEK_IPM_RN.RN_IPMP_VER_ULT_ITEM
	 * Verifica se último ítem do pacote está sendo removido
	 * @param quantidade
	 * @throws ApplicationBusinessException
	 */
	public void verificarUltimoItemPacoteMateriais(int quantidade) throws ApplicationBusinessException {
		if(quantidade==0){
			throw new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.SCE_00400);
		}
		
	}
	
	
	/**
	 * Remove o pacote de materiais
	 * @param pacote
	 * @throws BaseListException 
	 */
	@SuppressWarnings("deprecation")
	public void removerPacoteMateriais(ScePacoteMateriais pacote) throws BaseListException {
		pacote = getScePacoteMateriaisDAO().obterPorChavePrimaria(pacote.getId());
		validarExclusaoPacoteMateriais(pacote.getId());
		getScePacoteMateriaisDAO().remover(pacote);
	}
	
	/**
	 * @ORADB: SCEK_PMT_RN.RN_PMTP_VER_EXCLUSAO
	 * RN4: Realiza validações para exclusão do pacote de materiais
	 * @param idPacoteMateriais
	 * @throws BaseListException
	 */
	private void validarExclusaoPacoteMateriais(ScePacoteMateriaisId idPacoteMateriais) throws BaseListException{
		
		BaseListException erros = new BaseListException();
		
		final Long VALOR_CONTROLA_EXCLUSAO = 0l;
		
		Long quantidadeDependentesTransferencias = getManterTransferenciaMaterialRN().obterQuantidadeTransferenciasPorCentrosCustosNumeroPacoteMaterial(idPacoteMateriais);
		Long quantidadeDependentesRequisicao = getManterRequisicaoMaterialRN().obterQuantidadeRequisicaoMaterialPorCentrosCustosNumeroPacoteMaterial(idPacoteMateriais);
		Long quantidadeDependentesRequisicaoMaterialRetornos = getSceReqMaterialRetornosDAO().obterQuantidadeRequisicaoMateriaisRetornoPorCentrosCustosNumeroPacoteMaterial(idPacoteMateriais.getCodigoCentroCustoProprietario(), idPacoteMateriais.getCodigoCentroCustoAplicacao(), idPacoteMateriais.getNumero());
		Long quantidadeDependentesProducaoInternas = getSceProducaoInternaDAO().obterQuantidadeProducaoInternaPorCentroCustoNumeroPacoteMaterial(idPacoteMateriais.getCodigoCentroCustoProprietario(), idPacoteMateriais.getCodigoCentroCustoAplicacao(), idPacoteMateriais.getNumero());
		
		if(!VALOR_CONTROLA_EXCLUSAO.equals(quantidadeDependentesTransferencias)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_DEPENDENTES_TRANSFERENCIA));
		}
		if(!VALOR_CONTROLA_EXCLUSAO.equals(quantidadeDependentesRequisicao)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_DEPENDENTES_REQUISICAO));
		}
		if(!VALOR_CONTROLA_EXCLUSAO.equals(quantidadeDependentesRequisicaoMaterialRetornos)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_DEPENDENTES_REQUISICAO_MATERIAL_RETORNOS));
		}
		if(!VALOR_CONTROLA_EXCLUSAO.equals(quantidadeDependentesProducaoInternas)){
			erros.add(new ApplicationBusinessException(ManterPacoteMateriaisRNExceptionCode.ERRO_DEPENDENTES_PRODUCAO_INTERNAS));
		}
		if(erros.hasException()){
			throw erros;
		}
	}
	
	/**
	 * Realiza validações antes da persistência do pacote de materiais
	 * 
	 * @param itensPacote
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param seqAlmoxarifado
	 * @param seqEstoque
	 * @param numeroPacote
	 * @param situacao
	 * @throws BaseListException
	 */
	private void prePersistirPacoteMateriais(Set<SceItemPacoteMateriais> itensPacote, 
			Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, 
			Short seqAlmoxarifado, DominioSituacao situacao) throws BaseListException{
		
		BaseListException erros = new BaseListException();
		
		validarDadosObrigatoriosPacoteMaterial(erros, codigoCentroCustoProprietario, codigoCentroCustoAplicacao, seqAlmoxarifado, situacao);
		
		verificarAdicaoItemPacoteMateriais(erros, itensPacote.size());

		verificarAlmoxarifadoAtivo(erros, seqAlmoxarifado);
		
		verificarCentrosCustoAtivos(erros, codigoCentroCustoProprietario, codigoCentroCustoAplicacao);
		
		if(erros.hasException()){
			throw erros;
		}
	}
	
	/**
	 * Persiste no banco de dados o pacote de materiais, com ítens
	 * @param pacote
	 * @throws BaseListException
	 */
	public void persistirPacoteMateriais(ScePacoteMateriais pacote) throws BaseListException {
		
		if(pacote != null) {
			prePersistirPacoteMateriais(pacote.getItens(), 
					pacote.getId().getCodigoCentroCustoProprietario(), pacote.getId().getCodigoCentroCustoAplicacao(), 
					pacote.getAlmoxarifado().getSeq(), pacote.getIndSituacao());
					pacote.getId().setNumero(getScePacoteMateriaisDAO().obterProximoNumeroPacote(pacote.getId().getCodigoCentroCustoProprietario(), 
																								 pacote.getId().getCodigoCentroCustoAplicacao()));
					for(SceItemPacoteMateriais itemPacote: pacote.getItens()){
						itemPacote.getId().setNumeroPacoteMateriais(pacote.getId().getNumero());
					}
					getScePacoteMateriaisDAO().persistir(pacote);
		}
	}
	
	/**
	 * 
	 * Atualiza pacote material, com itens
	 * 
	 * @param pacote
	 * @throws BaseListException
	 */
	public void atualizarPacoteMaterial(ScePacoteMateriais pacote) throws BaseListException {
		
		// atualiza pacote materiais + itens do pacote		
		if(pacote != null) {

			// validacao
			prePersistirPacoteMateriais(pacote.getItens(), 
										pacote.getId().getCodigoCentroCustoProprietario(), 
										pacote.getId().getCodigoCentroCustoAplicacao(), 
										pacote.getAlmoxarifado().getSeq(), 
										pacote.getIndSituacao());
			
			getScePacoteMateriaisDAO().merge(pacote);
		}
	}

}
