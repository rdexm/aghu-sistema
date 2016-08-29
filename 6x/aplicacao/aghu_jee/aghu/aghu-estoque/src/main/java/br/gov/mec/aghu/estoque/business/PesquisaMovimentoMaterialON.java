package br.gov.mec.aghu.estoque.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioComparacaoDataCompetencia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class PesquisaMovimentoMaterialON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaMovimentoMaterialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

@Inject
private SceReqMateriaisDAO sceReqMateriaisDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 396754716072765755L;
	
	private enum PesquisaMovimentoMaterialONExceptionCode implements BusinessExceptionCode {
		COMPETENCIA_INVALIDA, PESQUISA_MOVIMENTO_INVALIDA;
	}

	/**
	 * Mátodo que realiza a pesquisa de datas de competência, por mes e ano, em estoque geral,
	 * com as validações necessárias
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MovimentoMaterialVO> pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(String parametro) throws ApplicationBusinessException{

		Integer ano = null, mes = null;

		if(parametro!=null){
			final String vlPesquisa = parametro;

			if (vlPesquisa != null && !StringUtils.isBlank(vlPesquisa)) {

				// 04 ou 4
				if ((vlPesquisa.length() == 1 || vlPesquisa.length() == 2)
						&& !Pattern.compile("[0-9]{1,2}").matcher(vlPesquisa)
						.matches()) {
					throw new ApplicationBusinessException(PesquisaMovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);

					// 2011
				} else if ((vlPesquisa.length() == 4)
						&& !Pattern.compile("[0-9]{4}").matcher(vlPesquisa)
						.matches()) {
					throw new ApplicationBusinessException(PesquisaMovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);

					// 3/82 ou 03/1982
				} else if ((vlPesquisa.length() > 4 && vlPesquisa.length() < 7)
						&& !Pattern.compile("[0-9]{1,2}/[0-9]{2,4}")
						.matcher(vlPesquisa).matches()) {
					throw new ApplicationBusinessException(PesquisaMovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);

					// 11/03/1982
				} else if ((vlPesquisa.length() > 7)
						&& !Pattern.compile("[0-9]{1,2}/[0-9]{2}/[0-9]{4}")
						.matcher(vlPesquisa).matches()) {
					throw new ApplicationBusinessException(PesquisaMovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);
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
							throw new ApplicationBusinessException(PesquisaMovimentoMaterialONExceptionCode.COMPETENCIA_INVALIDA);
						}
					}
				}
			}
		}
		
		return getSceMovimentoMaterialDAO().pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(mes, ano);
	
	}
	
	/**
	 * Valida se pelo menos um filtro foi informado
	 * @param id
	 * @param material
	 * @param dtGeracao
	 * @param almoxarifado
	 * @param centroCusto
	 * @param indEstorno
	 * @param fornecedor
	 * @param movimentoMaterialDataCompetencia
	 * @param nroDocGeracao
	 *  
	 */
	public void validaCampos(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao, SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor, SceMovimentoMaterial movimentoMaterialDataCompetencia, Integer nroDocGeracao) throws ApplicationBusinessException {
		
		if (tipoMovimento == null && material == null && dtGeracao == null && almoxarifado == null && centroCusto == null && fornecedor == null && movimentoMaterialDataCompetencia == null && nroDocGeracao == null) {
			
			throw new ApplicationBusinessException(PesquisaMovimentoMaterialONExceptionCode.PESQUISA_MOVIMENTO_INVALIDA);
			
		}
		
	}
	
	/**
	 * Pesquisa de Movimento de Materiais
	 * @param id
	 * @param material
	 * @param dtGeracao
	 * @param almoxarifado
	 * @param centroCusto
	 * @param indEstorno
	 * @param fornecedor
	 * @param movimentoMaterialDataCompetencia
	 * @param nroDocGeracao
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws BaseException 
	 * @throws ApplicationBusinessException
	 */
	public List<MovimentoMaterialVO> pesquisarMovimentosMaterial(SceTipoMovimento tipoMovimento, ScoMaterial material, Date dtGeracao, SceAlmoxarifado almoxarifado, FccCentroCustos centroCusto, DominioSimNao indEstorno, ScoFornecedor fornecedor, SceMovimentoMaterial movimentoMaterialDataCompetencia, DominioComparacaoDataCompetencia comparacaoDataCompetencia, Integer nroDocGeracao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) throws BaseException {
				
		List<SceMovimentoMaterial> listaMovimentos = getSceMovimentoMaterialDAO().pesquisarMovimentosMaterial(tipoMovimento, material, dtGeracao, almoxarifado, centroCusto, indEstorno, fornecedor, movimentoMaterialDataCompetencia, comparacaoDataCompetencia, nroDocGeracao, firstResult, maxResult, orderProperty, asc);
		List<MovimentoMaterialVO> retorno = new ArrayList<MovimentoMaterialVO>();
		
		for (SceMovimentoMaterial movimento : listaMovimentos) {
			
			MovimentoMaterialVO vo = new MovimentoMaterialVO();
			
			if (movimento.getId() != null && movimento.getId().getDtCompetencia() != null) {
				
				Calendar data = Calendar.getInstance();
				data.setTime(movimento.getId().getDtCompetencia());
				Integer mesCompetencia = data.get(Calendar.MONTH)+1;
				Integer anoCompetencia = data.get(Calendar.YEAR);
				
				vo.setMes(mesCompetencia.toString() + "/" + anoCompetencia.toString());
				vo.setId(movimento.getId());
				
			}
			
			if (movimento.getTipoMovimento() != null) {
				
				vo.setTipo(movimento.getTipoMovimento().getSigla());
				
				if (movimento.getTipoMovimento().getSigla().equals(("RM"))) {
					SceReqMaterial rm = this.getSceReqMateriaisDAO().obterPorChavePrimaria(movimento.getNroDocGeracao());
					
					if (rm != null && rm.getAtendimento() != null) {
						 vo.setNomePaciente(rm.getAtendimento().getPaciente().getNome());
						 vo.setNumeroAtendimento(rm.getAtendimento().getSeq());
						 vo.setNumeroProntuario(rm.getAtendimento().getPaciente().getProntuario());
					}
				}
				
			}
			
			if (movimento.getCentroCusto() != null) {
				
				vo.setCentroCusto(movimento.getCentroCusto().getCodigo());
				
			}
			
			if (movimento.getAlmoxarifado() != null) {
				
				vo.setAlmoxarifado(movimento.getAlmoxarifado().getSeq());
				
			}
			
			if (movimento.getFornecedor() != null) {
				
				vo.setFornecedor(movimento.getFornecedor().getNumero());
				
			}
			
			if (movimento.getMaterial() != null) {
				
				vo.setMaterial(movimento.getMaterial().getCodigo().toString());
				vo.setNomeMaterial(movimento.getMaterial().getNome());
				vo.setUnidade(movimento.getMaterial().getUnidadeMedida().getCodigo());
				
			}
			
			vo.setDocumento(movimento.getNroDocGeracao());
			vo.setEstornado(movimento.getIndEstorno());
			SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
			vo.setDtGeracao(sdf_1.format(movimento.getDtGeracao()));
			vo.setValor(movimento.getValor());
			vo.setQuantidade(movimento.getQuantidade());
			
			retorno.add(vo);
			
		}
		
		return retorno;
		
	}

	private SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}

	private SceReqMateriaisDAO getSceReqMateriaisDAO(){
		return sceReqMateriaisDAO;
	}

}
