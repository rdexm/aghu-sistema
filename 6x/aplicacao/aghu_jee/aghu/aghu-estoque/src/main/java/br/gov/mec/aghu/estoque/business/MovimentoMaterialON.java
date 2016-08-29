package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceItemNotaRecebimentoDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.DadosEntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.EntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.GrupoEntradaMateriasDiaVO;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceMovimentoMaterialId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MovimentoMaterialON extends BaseBusiness{

@EJB
private SceMovimentoMaterialRN sceMovimentoMaterialRN;

@EJB
private ScoMaterialRN scoMaterialRN;

private static final Log LOG = LogFactory.getLog(MovimentoMaterialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

@Inject
private SceItemNotaRecebimentoDAO sceItemNotaRecebimentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4257023928561369311L;

	public enum MovimentoMaterialONExceptionCode implements BusinessExceptionCode {
		COMPETENCIA_INVALIDA, DATA_GERACAO_MATERIAIS_DIA;
	}
	
	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}
	
	/**
	 * Inserir SceMovimentoMaterial
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	public void inserir(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador) throws BaseException{
		this.preInserir(sceMovimentoMaterial);
		this.getSceMovimentoMaterialRN().inserir(sceMovimentoMaterial, nomeMicrocomputador, true);
	}
	
	/**
	 * ORADB EVT_PRE_INSERT (INSERT)  
	 * Pré-inserir SceMovimentoMaterial
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	protected void preInserir(SceMovimentoMaterial sceMovimentoMaterial){
		sceMovimentoMaterial.setIndEstorno(false);
	}
	
	/**
	 * Mátodo que realiza a pesquisa de datas de competência, por mes e ano, em movimento de materiais,
	 * com as validações necessárias
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceMovimentoMaterial> pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(Object parametro) throws ApplicationBusinessException{
		
		Integer ano = null, mes = null;
		
		if(parametro!=null){
			final String vlPesquisa = (String) parametro;

			if (vlPesquisa != null && !StringUtils.isBlank(vlPesquisa)) {

			// 04 ou 4
			if ((vlPesquisa.length() == 1 || vlPesquisa.length() == 2)
					&& !Pattern.compile("[0-9]{1,2}").matcher(vlPesquisa)
							.matches()) {
				throw new ApplicationBusinessException(MovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);

				// 2011
			} else if ((vlPesquisa.length() == 4)
					&& !Pattern.compile("[0-9]{4}").matcher(vlPesquisa)
							.matches()) {
				throw new ApplicationBusinessException(MovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);

				// 3/82 ou 03/1982
			} else if ((vlPesquisa.length() > 4 && vlPesquisa.length() < 7)
					&& !Pattern.compile("[0-9]{1,2}/[0-9]{2,4}")
							.matcher(vlPesquisa).matches()) {
				throw new ApplicationBusinessException(MovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);

				// 11/03/1982
			} else if ((vlPesquisa.length() > 7)
					&& !Pattern.compile("[0-9]{1,2}/[0-9]{2}/[0-9]{4}")
							.matcher(vlPesquisa).matches()) {
				throw new ApplicationBusinessException(MovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);
			}

			
			final String[] comp = vlPesquisa.split("/");

			// 11/03/1982
			if (comp.length == 3) {
				mes = Integer.parseInt(comp[1]);
				ano = Integer.parseInt(comp[2]);

				// 03/1982
			} else if (comp.length == 2) {
				mes = Integer.parseInt(comp[0]);

				if (comp[1].length() == 2) {
					ano = Integer.parseInt("20" + comp[1]);
				} else {
					ano = Integer.parseInt(comp[1]);
				}

			} else {

				// 1982
				if (vlPesquisa.length() == 4) {
					ano = Integer.parseInt(vlPesquisa);

					// 01
				} else if (vlPesquisa.length() == 1 || vlPesquisa.length() == 2) {
					mes = Integer.parseInt(vlPesquisa);

				} else {
					if (vlPesquisa.indexOf('/') > 0) {
						mes = Integer.parseInt(vlPesquisa.substring(0,
								vlPesquisa.indexOf('/')));
					} else {
						throw new ApplicationBusinessException(MovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);
						}
					}
				}
			}
		}
		return getSceMovimentoMaterialDAO().pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(mes, ano);
	}
	
	public SceMovimentoMaterial obterMovimetnoMaterialPorId(SceMovimentoMaterialId mmtId){
		final Enum[] left = {SceMovimentoMaterial.Fields.ALMOXARIFADO, SceMovimentoMaterial.Fields.ALMOXARIFADO_COMPLEMENTO, 
					         SceMovimentoMaterial.Fields.CENTRO_CUSTO, SceMovimentoMaterial.Fields.CENTRO_CUSTO_REQUISITA,
					         SceMovimentoMaterial.Fields.MOTIVO_MOVIMENTO, SceMovimentoMaterial.Fields.SERVIDOR,
					         SceMovimentoMaterial.Fields.TIPO_MOVIMENTO, SceMovimentoMaterial.Fields.MATERIAL};		
		
		SceMovimentoMaterial movimento = getSceMovimentoMaterialDAO().obterPorChavePrimaria(mmtId, null, left);
		
		if (movimento.getServidor() != null) {
			LOG.info(movimento.getServidor().getPessoaFisica().getNome());
		}
		
		return movimento;
	}
	
	private SceMovimentoMaterialRN getSceMovimentoMaterialRN(){
		return sceMovimentoMaterialRN;
	}
	
	
	public EntradaMateriasDiaVO entradaMateriasDia(Date dataGeracao)throws ApplicationBusinessException{
		
		List<DadosEntradaMateriasDiaVO> primeiroResult = new ArrayList<DadosEntradaMateriasDiaVO>();
		List<DadosEntradaMateriasDiaVO> segundoResult = new ArrayList<DadosEntradaMateriasDiaVO>();
		EntradaMateriasDiaVO result = new EntradaMateriasDiaVO();
		result.setListaDadosEntradaMateriasDia(new ArrayList<DadosEntradaMateriasDiaVO>());
		result.setListaDadosEntradaMateriasDia(new ArrayList<DadosEntradaMateriasDiaVO>());
		result.setListaGrupoEntradaMateriasDia(new ArrayList<GrupoEntradaMateriasDiaVO>());
		
		primeiroResult = sceItemNotaRecebimentoDAO.entradaMateriasPrimeiraParte(dataGeracao);
		segundoResult = sceItemNotaRecebimentoDAO.entradaMateriasSegundaParte(dataGeracao);
		
		if (primeiroResult != null && !primeiroResult.isEmpty()){
			result.getListaDadosEntradaMateriasDia().addAll(primeiroResult);			
		}
		if (segundoResult != null && !segundoResult.isEmpty()){
			result.getListaDadosEntradaMateriasDia().addAll(segundoResult);			
		}
		
		List<GrupoEntradaMateriasDiaVO> grupoResult = new ArrayList<GrupoEntradaMateriasDiaVO>();
		
		grupoResult = scoMaterialRN.gerarListaGrupoEntradamateriaisDia(dataGeracao, result.getListaDadosEntradaMateriasDia());
		
		result.setListaGrupoEntradaMateriasDia(grupoResult);
		
		return result;
	}	
}
