package br.gov.mec.aghu.estoque.business;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.dominio.DominioTipoDespesa;
import br.gov.mec.aghu.estoque.dao.SceConsumoTotalMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
@Stateless
public class SceItemRmsRN extends BaseBusiness{

@EJB
private SceReqMateriaisRN sceReqMateriaisRN;

private static final Log LOG = LogFactory.getLog(SceItemRmsRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceConsumoTotalMateriaisDAO sceConsumoTotalMateriaisDAO;

@EJB
private IEstoqueFacade estoqueFacade;

@EJB
private IComprasFacade comprasFacade;

@Inject
private SceReqMateriaisDAO sceReqMateriaisDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private ICentroCustoFacade centroCustoFacade;


@EJB
private ICascaFacade cascaFacade;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private SceItemRmsDAO sceItemRmsDAO;

@Inject
private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3804365762807855113L;

	public enum SceItemRmsRNExceptionCode implements BusinessExceptionCode {
		SCE_00322,SCE_00324,SCE_00383,SCE_00325,SCE_00379,SCE_00292,SCE_00280,SCE_00319,SCE_00417,CENTRO_DE_CUSTO_NAO_ENCONTRADO,
		SCO_00755,SCO_00756,SCE_00839,SCE_00865,SCE_00877,SCE_00864,SCE_00878,SCE_00894,
		SCE_00878b, MENSAGEM_MATERIAL_GRUPO_RM_CATALOGO;
	}

	/*
	 * Métodos para Inserir SceItemRms
	 */

	/**
	 * ORADB SCET_IRM_BRI (INSERT)
	 * @param sceItemRms
	 * @throws BaseException
	 */
	private void preInserir(SceItemRms sceItemRms) throws BaseException{
		this.validarAlteracaoSceItemRms(sceItemRms, true); // RN1
		this.validarAlteracaoSceItemRmsEstoqueAlmoxarifado(sceItemRms); // RN2
	}

	/**
	 * Inserir SceItemRms
	 * @param sceItemRms
	 * @throws BaseException
	 */
	public void inserir(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException{
		this.preInserir(sceItemRms);
		this.getSceItemRmsDAO().persistir(sceItemRms);
		this.getSceItemRmsDAO().flush();
		this.posInserir(sceItemRms, nomeMicrocomputador);

	}

	/**
	 * ORADB SCEP_ENFORCE_IRM_RULES (INSERT)
	 * @param sceItemRms
	 * @throws BaseException
	 */
	private void posInserir(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException{
		// a regra deste metodo foi flexibilizada ao inves de utilizar parametros hardcoded.
		// esta configuracao esta agora ligada diretamente ao almoxarifado.
		//this.validarSceItemRmsTodosMateriaisMesmoGrupoMaterialAlmoxarifadoCentral(sceItemRms.getSceReqMateriais()); // RN1
		this.atualizarSceItemRmsGrupoMaterialSemGrupo(sceItemRms, nomeMicrocomputador); // RN2
		this.validarSceItemRmsMaterialTipoDespesaCentroCusto(sceItemRms); // RN3
	}
	
	public Boolean habilitarEdicao(SceItemRms sceItemRms) {
		Boolean ret = Boolean.FALSE;
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		FccCentroCustos ccAtuacao = getCentroCustoFacade().obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao();
		Boolean isAlmoxarife = getCascaFacade(). usuarioTemPerfil(servidorLogado.getUsuario(), "ADM29");
		
		// se eh o usuario solicitante...
		if (Objects.equals(sceItemRms.getSceReqMateriais().getServidor(),servidorLogado) || Objects.equals(sceItemRms.getSceReqMateriais().getCentroCusto(), ccAtuacao)) {
			
			if (sceItemRms.getSceReqMateriais().getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
				ret = true;	
			}
		}
		
		// se eh o almoxarife
		if (Objects.equals(sceItemRms.getSceReqMateriais().getAlmoxarifado().getCentroCusto(), ccAtuacao) && isAlmoxarife) {
			if (sceItemRms.getSceReqMateriais().getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G) || sceItemRms.getSceReqMateriais().getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
				ret = true;				
			}
		}
		return ret;
	}

	/*
	 * Métodos para Atualizar SceItemRms
	 */

	/**
	 * ORADB SCET_IRM_BRU (UPDATE)
	 * @param sceItemRms
	 * @throws BaseException
	 */
	private void preAtualizar(SceItemRms sceItemRms) throws BaseException{

		this.validarAlteracaoSceItemRms(sceItemRms, false); // RN1	

		final SceItemRms sceItemRmsOriginal = this.getSceItemRmsDAO().obterOriginal(sceItemRms);

		final String codigoScoUnidadeMedidaOriginal = sceItemRmsOriginal.getScoUnidadeMedida().getCodigo();
		final Integer seqSceEstoqueAlmoxarifadoOriginal = sceItemRmsOriginal.getEstoqueAlmoxarifado().getSeq();
		final Integer qtdeEntregueOriginal = sceItemRmsOriginal.getQtdeEntregue();
        if((codigoScoUnidadeMedidaOriginal != null && !codigoScoUnidadeMedidaOriginal.equals(sceItemRms.getScoUnidadeMedida().getCodigo())) 
				|| (seqSceEstoqueAlmoxarifadoOriginal !=null && !seqSceEstoqueAlmoxarifadoOriginal.equals(sceItemRms.getEstoqueAlmoxarifado().getSeq())) 
				|| (!this.habilitarEdicao(sceItemRms) && (qtdeEntregueOriginal != null && !qtdeEntregueOriginal.equals(sceItemRms.getQtdeEntregue())))){
			this.validarAlteracaoSceItemRmsEstoqueAlmoxarifado(sceItemRms);  // RN2
		}

	}

	/**
	 * Atualizar SceItemRms
	 * @param sceItemRms
	 * @throws BaseException
	 */
	public void atualizar(SceItemRms sceItemRms) throws BaseException{
		this.preAtualizar(sceItemRms);
		this.getSceItemRmsDAO().merge(sceItemRms);		
		this.posAtualizar(sceItemRms);
	}

	/**
	 * ORADB SCEP_ENFORCE_IRM_RULES (UPDATE)
	 * @param sceItemRms
	 * @throws BaseException
	 */
	private void posAtualizar(SceItemRms sceItemRms) throws BaseException{
		// a regra deste metodo foi flexibilizada ao inves de utilizar parametros hardcoded.
		// esta configuracao esta agora ligada diretamente ao almoxarifado.
		//this.validarSceItemRmsTodosMateriaisMesmoGrupoMaterialAlmoxarifadoCentral(sceItemRms.getSceReqMateriais()); // RN1
		this.validarSceItemRmsMaterialTipoDespesaCentroCusto(sceItemRms); // RN2
	}

	/*
	 * Métodos para Remover SceItemRms
	 */	

	/**
	 * ORADB SCEP_ENFORCE_IRM_RULES (DELETE)
	 * @param sceItemRms
	 * @throws BaseException
	 */
	private void preRemover(SceItemRms sceItemRms, Integer countItensLista) throws BaseException{
		//necessario comentar para permitir a troca de material de um item de RM.
		// garantia foi levada para a tela, que agora cancela a RM quando o ultimo item eh excluido.
		//this.validarRemoverSceItemRmsUltimoItemSceReqMateriais(sceItemRms.getSceReqMateriais(), countItensLista); // RN1
	}

	/**
	 * Remover SceItemRms
	 * @param sceItemRms
	 * @throws BaseException
	 */
	public void remover(SceItemRms sceItemRms, Integer countItensLista) throws BaseException{
		this.preRemover(sceItemRms, countItensLista);
		sceItemRms = this.getSceItemRmsDAO().obterPorChavePrimaria(sceItemRms.getId());
		this.getSceItemRmsDAO().remover(sceItemRms);
		this.getSceItemRmsDAO().flush();
	}

	/*
	 * RNs 
	 */

	/**
	 * ORADB SCEK_IRM_RN.RN_IRMP_VER_GRP_MAT
	 * Verifica se todos os materiais são do mesmo grupo material para almoxarifado central
 	* ATENCAO: a regra mudou durante a implantacao no HCPA. Foi acrescentado um cadastro auxiliar
	 *          ligado ao cadastro do almoxarifado, que flexibiliza esta validacao
	 *  
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void validarSceItemRmsTodosMateriaisMesmoGrupoMaterialAlmoxarifadoCentral(SceReqMaterial sceReqMateriais) throws BaseException{

		Integer codigoGrupoMaterialPrimeiroItem = null;
		Boolean isMaterialEstocavel = null;
		Long codigoClassificacaoMaterial = null;

		// Verifica se o almoxarifado da requisição de material é central
		if (sceReqMateriais.getAlmoxarifado() != null && sceReqMateriais.getAlmoxarifado().getIndCentral()){

			List<SceItemRms> itens = getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriais(sceReqMateriais.getSeq());
			// Instancia uma lista que armazenará o valor numérico dos parâmetros do AGH
			List<Integer> listaParametrosGrupos = new LinkedList<Integer>();

			// Acrescenta na lista o valor numérico do parametro para grupo de rouparia
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_ROUPARIA, listaParametrosGrupos);

			// Acrescenta na lista o valor numérico do parametro para grupo de material de expediente
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_MAT_EXPEDIENTE, listaParametrosGrupos);

			// Acrescenta na lista o valor numérico do parametro para grupo de  higiene e limpeza
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_HIGIENE_LIMPEZA, listaParametrosGrupos);

			// Acrescenta na lista o valor numérico do parametro para grupo de material de informática
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_MAT_INFORMATICA, listaParametrosGrupos);

			// Acrescenta na lista o valor numérico do parametro para grupo de material de utilização gráfica
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_MAT_UTILIZ_GRAFICA, listaParametrosGrupos);

			// Acrescenta na lista o valor numérico do parametro para grupo de material de áudio e vídeo
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_MAT_AUDIO_VIDEO, listaParametrosGrupos);		

			/**
			 * Os parâmetros abaixo estão HARDCODE no HCPA
			 */

			/* Acrescenta na lista o valor numérico do parametro para grupo de material de áudio e vídeo
			Valor HARDCODE HCPA = 6 */
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_EQUIP_PROTECAO, listaParametrosGrupos); 

			/* Acrescenta na lista o valor numérico do parametro para grupo de material de áudio e vídeo
			Valor HARDCODE HCPA = 34 */
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_UTENSILIOS_DIVERSOS, listaParametrosGrupos); 

			/* Acrescenta na lista o valor numérico do parametro para grupo de material de áudio e vídeo
			Valor HARDCODE HCPA = 36 */
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_PROD_INT_GRAFICA, listaParametrosGrupos); 

			/*Acrescenta na lista o valor numérico do parametro para grupo de material de áudio e vídeo
			Valor HARDCODE HCPA = 18 */
			this.addGrupoParametroLista(AghuParametrosEnum.P_GR_BRINQUEDOS, listaParametrosGrupos); 

			// Percorre os itens da requisição de material
			for (SceItemRms itemRms : itens) {

				// Obtem o codigo do grupo de material
				if (codigoGrupoMaterialPrimeiroItem == null && itemRms.getSceReqMateriais().getGrupoMaterial() != null){

					codigoGrupoMaterialPrimeiroItem = itemRms.getSceReqMateriais().getGrupoMaterial().getCodigo();

				}

				// Obtem o status quanto ao estoque do material
				if (isMaterialEstocavel == null && itemRms.getEstoqueAlmoxarifado().getMaterial() != null){

					isMaterialEstocavel = itemRms.getEstoqueAlmoxarifado().getMaterial().getIndEstocavelBoolean();

				}

				// Regata o código do grupo de material do SceItemRms
				final Integer codigoGrupoMaterialSceItemRms = (sceReqMateriais.getGrupoMaterial()!=null)?sceReqMateriais.getGrupoMaterial().getCodigo():null;

				/* Procura ocorrências do código do grupo de material do SceItemRms na lista de valores dos parâmetros do AGH
				 * Se o grupo dos próximos itens é um destes e o grupo do primeiro item não é um destes, 
				 * é porque não são de grupos combináveis.*/

				if (listaParametrosGrupos.contains(codigoGrupoMaterialSceItemRms)) {

					// Valida: Não é permitido requisitar materiais destes diferentes grupos de materiais
					if (!listaParametrosGrupos.contains(codigoGrupoMaterialPrimeiroItem)){

						throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00877);

					}

				} else {

					// Testa se os materiais dos itens pertencem ao mesmo grupo de material
					if (codigoGrupoMaterialSceItemRms!=null && codigoGrupoMaterialPrimeiroItem != null && !codigoGrupoMaterialSceItemRms.equals(codigoGrupoMaterialPrimeiroItem)){

						throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00864);

					}

				}

				// Valida: Os materiais de um Item devem ser só estocáveis ou só diretos
				if (itemRms.getEstoqueAlmoxarifado().getMaterial() != null && !itemRms.getEstoqueAlmoxarifado().getMaterial().getIndEstocavelBoolean().equals(isMaterialEstocavel)){ // TODO

				if (!itemRms.getEstoqueAlmoxarifado().getMaterial().getEstocavel()) {
						throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00878b);
					} else {
						throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00878);	
					}

				}

			}

		}  else{ // Caso o almoxarifado da requisição de material NAO for central

			AghParametros parametroAlmoxarifadoFarmaciaCentral = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ALMOX_FARM_CENTRAL);
			Short valorNumericoAlmoxarifadoFarmaciaCentral = null;
			if(parametroAlmoxarifadoFarmaciaCentral != null && parametroAlmoxarifadoFarmaciaCentral.getVlrNumerico() != null){
				valorNumericoAlmoxarifadoFarmaciaCentral = parametroAlmoxarifadoFarmaciaCentral.getVlrNumerico().shortValue();
			}

			// Verifica se o id do almoxarifado da requisição de material é igual ao parâmetro de almoxarifado de farmácia central
			if(sceReqMateriais.getAlmoxarifado().getSeq().equals(valorNumericoAlmoxarifadoFarmaciaCentral)){

				AghParametros parametroClasseMaterialSoro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLASS_MAT_SOROS);

				// Código (mínimo e máximo) de classificação de material
				Long codigoClassificacao = null;
				Long codigoClassificacaoMaxima = null;

				if(parametroClasseMaterialSoro != null && parametroClasseMaterialSoro.getVlrTexto() != null){
					// Popula código (mínimo e máximo) de classificação de material
					codigoClassificacao = Long.valueOf(parametroClasseMaterialSoro.getVlrTexto());
					codigoClassificacaoMaxima = Long.valueOf(parametroClasseMaterialSoro.getVlrTexto().replace("00","99"));
				}

				// Percorre os itens da requisição de material
				List<SceItemRms> listaItensRequisicaoMaterial = getEstoqueFacade().pesquisarListaSceItemRmsPorSceReqMateriais(sceReqMateriais.getSeq());
				for (SceItemRms itemRms : listaItensRequisicaoMaterial) {

					if (codigoClassificacaoMaterial == null){

						final Integer scoMaterialCodigo = itemRms.getEstoqueAlmoxarifado().getMaterial().getCodigo();
						ScoMateriaisClassificacoes scoMateriaisClassificacoes = this.getComprasFacade().buscarPrimeiroScoMateriaisClassificacoesPorMaterial(scoMaterialCodigo);
						if(scoMateriaisClassificacoes != null){
							codigoClassificacaoMaterial = scoMateriaisClassificacoes.getId().getCn5Numero();
						}

					}

					ScoMaterial material = itemRms.getEstoqueAlmoxarifado().getMaterial();

					if(codigoClassificacaoMaterial !=null && material != null){

						List<ScoMateriaisClassificacoes> listaMateriaisClassificacoes = this.getComprasFacade().pesquisarScoMateriaisClassificacoesPorMaterial(material.getCodigo());

						// Percorre as classificações de material
						for (ScoMateriaisClassificacoes scoMateriaisClassificacoes: listaMateriaisClassificacoes) {

							// Valida a classificação do item de requisição

							if(validarClassificacaoRequisicaoSoros(codigoClassificacao,codigoClassificacaoMaxima,scoMateriaisClassificacoes.getId().getCn5Numero())){

								// Valida: Requisição de soros só deve conter materiais desta classificação
								if(!validarClassificacaoRequisicaoSoros(codigoClassificacao,codigoClassificacaoMaxima,codigoClassificacaoMaterial)){
									throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00894);
								}

							} else{
								// Valida: Requisição de soros só deve conter materiais desta classificação
								if(validarClassificacaoRequisicaoSoros(codigoClassificacao,codigoClassificacaoMaxima,codigoClassificacaoMaterial)){
									throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00894);
								}								
							}
						}
					}
				}				
			} 			
		}
	}

	/**
	 * ORADB SCEK_IRM_RN.RN_IRMP_ATU_GRP_MAT
	 * Atualiza o grupo de material quando requisão de material não tiver grupo informado
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarSceItemRmsGrupoMaterialSemGrupo(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException{

		final Integer seqReqMaterial = sceItemRms.getSceReqMateriais().getSeq();
		final Short seqAlmoxarifado = sceItemRms.getSceReqMateriais().getAlmoxarifado().getSeq();
		final SceReqMaterial sceReqMateriais = this.getSceReqMateriaisDAO().buscarSceReqMateriaisPorAlmoxarifado(seqReqMaterial, seqAlmoxarifado);

		// Verifica existencia da requisição de material
		if(sceReqMateriais == null){
			throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00839);
		}

		// Verifica se o almoxarifado da requisição de material é central
		if (sceReqMateriais.getAlmoxarifado().getIndCentral()) {

			SceItemRms itemRms = this.getSceItemRmsDAO().buscarSceItemRmsPorGrupoMaterialSemGrupo(sceItemRms.getSceReqMateriais().getSeq());

			// Verifica a existencia do item da requisição de material
			if (itemRms != null) {

				// Instancia uma lista que armazenará o valor numérico dos parâmetros do AGH
				List<Integer> listaParametrosGrupos = new LinkedList<Integer>();

				// Acrescenta na lista o valor numérico do parametro para grupo de rouparia
				this.addGrupoParametroLista(AghuParametrosEnum.P_GR_ROUPARIA, listaParametrosGrupos);

				// Acrescenta na lista o valor numérico do parametro para grupo de material de expediente
				this.addGrupoParametroLista(AghuParametrosEnum.P_GR_MAT_EXPEDIENTE, listaParametrosGrupos);

				// Acrescenta na lista o valor numérico do parametro para grupo de  higiene e limpeza
				this.addGrupoParametroLista(AghuParametrosEnum.P_GR_HIGIENE_LIMPEZA, listaParametrosGrupos);

				// Atualiza a requisição de materal com o grupo de material do SceItemRms localizado
				if (!listaParametrosGrupos.contains(itemRms.getEstoqueAlmoxarifado().getMaterial().getGrupoMaterial().getCodigo()) && itemRms.getSceReqMateriais().getGrupoMaterial() == null) {

					// #42059 - Comentado o setGrupoMaterial, para que o grupo de material seja gravado somente quando o usuário informar
					//sceReqMateriais.setGrupoMaterial(itemRms.getEstoqueAlmoxarifado().getMaterial().getGrupoMaterial());
					this.getSceReqMateriaisRN().atualizar(sceReqMateriais, nomeMicrocomputador);

				}

			} else {

				throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00865);

			}

		} 

	} 


	/**
	 * ORADB SCEK_IRM_RN.RN_IRMP_VER_TIP_DESP
	 * Verifica se o tipo de despesa do centro de custo aceita o material
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException 
	 */
	public void validarSceItemRmsMaterialTipoDespesaCentroCusto(SceItemRms sceItemRms) throws BaseException{

		// Pesquisa SceItemRms relacionada a um centro de custo
		final SceItemRms sceItemRmsCentroCusto = this.getSceItemRmsDAO().buscarSceItemRmsPorMaterialCompativelTipoDespesaCentroCusto(sceItemRms);	

		if(sceItemRmsCentroCusto != null){

			final DominioTipoDespesa indTipoDespesa = sceItemRmsCentroCusto.getSceReqMateriais().getCentroCusto().getIndTipoDespesa();

			// Verifica se o tipo de despesa é serviço. Centro de Custo de Serviço não aceita  requisição de material
			if(DominioTipoDespesa.S.equals(indTipoDespesa)){
				throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCO_00756);
			}

			if(sceItemRmsCentroCusto.getSceReqMateriais()!=null && sceItemRmsCentroCusto.getSceReqMateriais().getGrupoMaterial()!=null){ //ScoGrupoMaterial pode ser null
				final Boolean indEngenharia = sceItemRmsCentroCusto.getSceReqMateriais().getGrupoMaterial().getEngenhari();

				// Verifica se o tipo de despesa é obra e se o material do grupo difere do tipo engenharia
				if(DominioTipoDespesa.O.equals(indTipoDespesa) && !indEngenharia){
					throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCO_00755);
				}

			}

		} else{
			throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.CENTRO_DE_CUSTO_NAO_ENCONTRADO);
		}

	}

	/**
	 * ORADB SCEK_IRM_RN.RN_IRMP_VER_ALTERAC
	 * @param sceItemRms
	 * @param isInserir determina o tipo de evento que delega as regras: true = inserir, false = atualizacao/update
	 * @throws ApplicationBusinessException
	 */
	public void validarAlteracaoSceItemRms(SceItemRms sceItemRms, boolean isInserir) throws ApplicationBusinessException{

		final SceItemRms sceItemRmsOriginal = this.getSceItemRmsDAO().obterPorChavePrimaria(sceItemRms.getId());

		// Testa se o evento é: INSERIR
		if(isInserir){

			// SCE_00417 Verifica se a situação do material de requisão é G (Gerada) e se possuí estoque
			if(DominioSituacaoRequisicaoMaterial.G.equals(sceItemRms.getSceReqMateriais().getIndSituacao()) 
					&& !sceItemRms.getIndTemEstoque()){
				throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00417);
			}

		} else{ // Caso o evento é uma ATUALIZAÇÃO

			// SCE_00324 Verifica se a situação do material de requisão é C (Confirmada)
			if(DominioSituacaoRequisicaoMaterial.C.equals(sceItemRms.getSceReqMateriais().getIndSituacao())){

				final Integer qtdeRequisitadaOriginal = sceItemRmsOriginal.getQtdeRequisitada();
				final Integer ealSeqOriginal = sceItemRmsOriginal.getId().getEalSeq();
				final ScoUnidadeMedida scoUnidadeMedidaOriginal = sceItemRmsOriginal.getScoUnidadeMedida();

				// Verifica modificações na quantidade requisitada, estoque almoxarifado e unidade de medida
				if((!qtdeRequisitadaOriginal.equals(sceItemRms.getQtdeRequisitada()) && !this.habilitarEdicao(sceItemRms)) ||
						!ealSeqOriginal.equals(sceItemRms.getId().getEalSeq()) ||
						!scoUnidadeMedidaOriginal.equals(sceItemRms.getScoUnidadeMedida())){

					throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00324);
				}
			}

			// SCE_00383 Verifica se a situação do material de requisão é G (Gerada)
			if(DominioSituacaoRequisicaoMaterial.G.equals(sceItemRms.getSceReqMateriais().getIndSituacao())){

				final Integer qtdeEntregueOriginal = sceItemRmsOriginal.getQtdeEntregue();

				// Verifica modificações na quantidade requisitada, estoque almoxarifado e unidade de medida
				if(qtdeEntregueOriginal != null && !qtdeEntregueOriginal.equals(sceItemRms.getQtdeEntregue())){

					throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00383);
				}
			}	

		}

		/**
		 * As validações a seguir são compartilhadas por ambos tipos de eventos (Inserir/Alterar)
		 */

		//  SCE_00325 Verifica se a situação do material de requisão é A (Cancelada)
		if(DominioSituacaoRequisicaoMaterial.A.equals(sceItemRms.getSceReqMateriais().getIndSituacao())){
			throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00325);
		}

		// SCE_00379 Verifica se NÃO possuí estoque e se a quantidade entregue é nula
		if(!sceItemRms.getIndTemEstoque() && sceItemRms.getQtdeEntregue() == null){
			throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00379);
		}

	}	

	/**
	 * ORADB SCEK_IRM_RN.RN_IRMP_VER_ESTQ_ALM
	 * Valida alterações em sceItemRms relacionadas ao almoxarifado
	 * @param sceItemRms
	 * @throws ApplicationBusinessException
	 */
	public void validarAlteracaoSceItemRmsEstoqueAlmoxarifado(SceItemRms sceItemRms) throws ApplicationBusinessException{

		//  Consulta a existência de registros de estoque almoxarifado e requisição de material
		final SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoPorId(sceItemRms.getEstoqueAlmoxarifado().getSeq());
		//final SceReqMateriais sceReqMateriais = this.getSceReqMateriaisDAO().buscarSceReqMateriaisPorId(sceItemRms.getSceReqMateriais().getSeq());

		// Vetifica se os registros de estoque almoxarifado e requisição de material são válidos
		if(sceEstoqueAlmoxarifado != null){
			final String codigoUnidadeMedidaSceEstoqueAlmoxarifado =  sceEstoqueAlmoxarifado.getUnidadeMedida().getCodigo();

			// Verifica modificações na unidade medida do estoque almoxarifado
			if(!codigoUnidadeMedidaSceEstoqueAlmoxarifado.equals(sceItemRms.getScoUnidadeMedida().getCodigo())){
				throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00280);
			}
		} else{
			// Estoque Almoxarifado não cadastrado ou Inativo
			throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00292);
		}

	}

	/**
	 * ORADB SCEK_IRM_RN.RN_IRMP_VER_ULT_ITEM
	 * Valida a remoção de um SceItemRms. Se o registro de SceItemRms for o último de um SceReqMateriais, o mesmo NÃO será removido.   
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException 
	 */
	public void validarRemoverSceItemRmsUltimoItemSceReqMateriais(SceReqMaterial sceReqMateriais, Integer countItensLista) throws ApplicationBusinessException{

		final Long quandidadeSceItemRmsPorSceReqMateriais = this.getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriaisCount(sceReqMateriais);

		// Verifica se o SceItemRms é o último de uma requisição de material
		if(quandidadeSceItemRmsPorSceReqMateriais != null && quandidadeSceItemRmsPorSceReqMateriais == 1 && countItensLista==1){
			throw new ApplicationBusinessException(SceItemRmsRNExceptionCode.SCE_00322);
		}
	}


	/**
	 * ORADB SCEK_RMS_RN.RN_RMSP_VER_SLDO_TER
	 * Verifica para cada Item se Saldo Hcpa é suficiente.  
	 * Se não suficiente e existir Saldo de Terceiros suficiente para completar a Qtde Entg, atualiza a Qtde Entregue para o Saldo Hcpa, 
	 * o que permite posteriormente completar o atendimento através de Saldo de Terceiros  (Ver RN_RMSP_GERA_RM_TERC) 
	 * O Saldo de Terceiros precisa estar Disponível.  Se Bloqueado uma mensagem será emitida sugerindo o desbloqueio. 
	 * @param sceReqMateriais
	 * @throws BaseException 
	 */
	public void verificarSaldoTerceiros(SceReqMaterial sceReqMateriais) throws BaseException {
		List<SceItemRms> listaItens = getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriais(sceReqMateriais.getSeq());
		List<SceItemRms> listaItensOriginal = new ArrayList<SceItemRms>();
		SceEstoqueAlmoxarifado estoqueAlm = listaItens != null && !listaItens.isEmpty() ? listaItens.get(0).getEstoqueAlmoxarifado() : null;

		if(estoqueAlm != null && estoqueAlm.getFornecedor() == null){			
			listaItens =  getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriaisDadosEstoqueTerceiros(sceReqMateriais);
			for(SceItemRms item : listaItens){
				if(item.getQtdeEntregue() != null && item.getEstoqueAlmoxarifado().getQtdeDisponivel() != null){
					Integer saldoFornecedores = getSceEstoqueAlmoxarifadoDAO().obterQuantidadeEstoqueAlmorarifaxoPorMaterialAlmoxarifado(item.getEstoqueAlmoxarifado().getMaterial(),item.getEstoqueAlmoxarifado().getAlmoxarifado());
					if( saldoFornecedores >=  (item.getQtdeEntregue() - item.getEstoqueAlmoxarifado().getQtdeDisponivel()) ){
						listaItensOriginal.add(item);
					}
				}

				Integer qntDisponivel = item.getEstoqueAlmoxarifado().getQtdeDisponivel() != null ? item.getEstoqueAlmoxarifado().getQtdeDisponivel() : 0;
				Integer qntTerceiros = item.getQtdeEntregue() != null ? item.getQtdeEntregue() : 0;

				item.setQtdeEntregue(qntDisponivel);
				item.setQtdeTerceiros(qntDisponivel - qntTerceiros);

				atualizar(item);
			}

		}
	}

	/**
	 * 
	 * @param sceItemRmses
	 * @param codigoCCAplicacao
	 * @throws ApplicationBusinessException
	 */
	public void preencheConsumoMedioItemRequisicao(SceItemRms sceItemRms, Integer codigoCCAplicacao) throws ApplicationBusinessException{

		AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		obterConsumos(sceItemRms, param.getVlrData(), sceItemRms.getEstoqueAlmoxarifado().getMaterial().getCodigo(), codigoCCAplicacao);

	}

	/**
	 * Obtem consumos de materiais.
	 * Transcrição da procedure BUSCA_CONSUMO_MEDIO_SEMESTRE (forms refere à história 595).
	 * @return
	 * @author fabio.winck
	 */
	public void obterConsumos(SceItemRms sceItemRms, Date dataCompetencia,	Integer codigoMaterial, Integer codigoCCAplicacao) {

		Double consUlt30Dias = 0.0;
		Double consumoMesAnterior = 0.0;
		Integer consumoMesAtual = 0;
		Integer consumoSemestre = 0;
		Double consumoMedioSemestre = 0.0;

		// obtem consumo mes atual
		consumoMesAtual = getSceConsumoTotalMateriaisDAO().obterConsumoNoMes(
				codigoMaterial, codigoCCAplicacao, dataCompetencia);

		if (consumoMesAtual == null) {
			consumoMesAtual = 0;
		}

		// Obter o dia atual
		Calendar cal = Calendar.getInstance();
		Integer diaHoje = cal.get(Calendar.DAY_OF_MONTH);

		Integer diasMesAnt = 30 - diaHoje; // Considera sempre 30 dias de
		// consumo

		Date dataInicio;
		Date dataFim;

		if (diasMesAnt > 0) {
			cal.setTime(dataCompetencia);

			// Obtem data fim (ultimo dia do mes passado)
			cal.set(Calendar.DAY_OF_MONTH, 1); // vai pro primeiro dia deste mes
			cal.add(Calendar.DAY_OF_MONTH, -1); // tira um dia, indo para o
			// ultimo dia do mes passado
			// zera horas
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			dataFim = cal.getTime();

			// Obtem datas de incio do consumo do mes passado (soh pega os dias
			// do mes passado cuja soma com os dias do mes atual darao 30)
			cal.add(Calendar.DAY_OF_MONTH, (diasMesAnt) * -1);
			dataInicio = cal.getTime();
			// obtem consumo do mes anterior
			consumoMesAnterior = getSceMovimentoMaterialDAO().obterConsumoPassadoPorTmvEstorno(dataInicio, dataFim,
					codigoMaterial, codigoCCAplicacao);

			if (consumoMesAnterior == null) {
				consumoMesAnterior = 0.0;
			}
		}

		consUlt30Dias = consumoMesAtual + consumoMesAnterior;

		// Adiciona no obj o consumo dos ultimos trinta dias
		sceItemRms.setMediaTrintaDias(new DecimalFormat("######.##").format(consUlt30Dias));
		// prepara datas para obter do ultimo semestre
		cal.setTime(dataCompetencia); // volta a data para a data de competencia
		cal.add(Calendar.MONTH, -1);// mes final do periodo
		dataFim = cal.getTime();

		// volta para a data de inicio de 6 meses antes do mes anterior a
		// competencia
		cal.add(Calendar.MONTH, -6);
		// zera horas
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);

		dataInicio = cal.getTime();

		// Obtem consumo dos ultimos 6 meses
		consumoSemestre = getSceConsumoTotalMateriaisDAO()
		.obterConsumoNoPeriodo(codigoMaterial, codigoCCAplicacao,
				dataInicio, dataFim);

		if (consumoSemestre != null && consumoSemestre > 0) {

			// Obtem o consumo medio do semestre
			consumoMedioSemestre = consumoSemestre.doubleValue() / 6;
		}

		// Adiciona no obj o consumo dos ultimos trinta dias
		sceItemRms.setMediaSemestre(new DecimalFormat("######.##").format(consumoMedioSemestre));
	}

	/**
	 * Verifica a existência do item para a requisição de material
	 * */
	public boolean verificaExistencia(SceItemRms elementoModificado) {
		return getSceItemRmsDAO().verificaExistencia(elementoModificado);
	}

	/**
	 * Métodos utilitários
	 */

	/**
	 * Método utilitário para acrescentar o valor númerico de uma instância AghParametros em uma lista.
	 * @param aghuParametrosEnum
	 * @param listaAghuParametros
	 * @throws BaseException
	 */
	private void addGrupoParametroLista(AghuParametrosEnum aghuParametrosEnum, List<Integer> listaAghuParametros) throws BaseException{
		final AghParametros aghParametro = this.getParametroFacade().buscarAghParametro(aghuParametrosEnum);
		if(aghParametro != null && aghParametro.getVlrNumerico() != null){
			listaAghuParametros.add(aghParametro.getVlrNumerico().intValue());
		}
	}

	/**
	 * Método utilitário para validar a classificação de materiais da requisição de soros
	 * @param codigoClassificacao
	 * @param codigoClassificacaoMaxima
	 * @param vCn5Numero
	 * @return
	 */
	private boolean validarClassificacaoRequisicaoSoros(Long codigoClassificacao, Long codigoClassificacaoMaxima, Long codigoClassificacaoMaterial) {
		if (codigoClassificacaoMaterial.longValue() >= codigoClassificacao.longValue() && codigoClassificacaoMaterial.longValue() <= codigoClassificacaoMaxima.longValue()) {
			return true;
		}
		return false;
	}
	
	public List<SceItemRms> pesquisarListaSceItemRmsPorSceReqMateriais(Integer reqMaterialSeq){
		return this.getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriais(reqMaterialSeq);
	}

	/**
	 * get de RNs e DAOs
	 */

	protected SceItemRmsDAO getSceItemRmsDAO(){
		return sceItemRmsDAO;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected SceReqMateriaisDAO getSceReqMateriaisDAO(){
		return sceReqMateriaisDAO;
	}

	protected SceReqMateriaisRN getSceReqMateriaisRN(){
		return sceReqMateriaisRN;
	}

	protected SceConsumoTotalMateriaisDAO getSceConsumoTotalMateriaisDAO(){
		return sceConsumoTotalMateriaisDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	protected void setCentroCustoFacade(ICentroCustoFacade centroCustoFacade) {
		this.centroCustoFacade = centroCustoFacade;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected void setCascaFacade(ICascaFacade cascaFacade) {
		this.cascaFacade = cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	protected void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
	
	

}