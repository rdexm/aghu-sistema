package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.CadastroMateriaisImpressaoNotaSalaVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialImpNotaSalaUnDAO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMS para #24345 – Associar materiais para impressão de nota de sala
 * 
 * @author aghu
 * 
 */
@Stateless
public class MateriaisImpressaoNotaSalaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MateriaisImpressaoNotaSalaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMaterialImpNotaSalaUnDAO mbcMaterialImpNotaSalaUnDAO;


	@EJB
	private IComprasFacade iComprasFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcMaterialImpNotaSalaUnRN mbcMaterialImpNotaSalaUnRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5101844175847716218L;

	/**
	 * ORADB PROCEDURE EVT_WHEN_VALIDATE_ITEM
	 * 
	 * @param materialImpNotaSalaUn
	 * @throws BaseException
	 */
	public void validarItemMaterialNotaSala(MbcMaterialImpNotaSalaUn materialImpNotaSalaUn) throws BaseException {

		final ScoMaterial material = materialImpNotaSalaUn.getMaterial();
		if (material != null && StringUtils.isEmpty(materialImpNotaSalaUn.getNomeImpressao())) {
			/*
			 * Se o material for diferente de nulo e nome impressão estiver em branco (NOME_IMP), então seta o valor da descrição do material
			 * Caso o material não possua descrição é setado o nome do material.
			 */
			if (material.getDescricao() != null) {
				materialImpNotaSalaUn.setNomeImpressao(material.getDescricao());
			} else {
				materialImpNotaSalaUn.setNomeImpressao(formatarNomeImpressaoDescricaoMaterial(material.getNome()));
			}
		}

		final ScoUnidadeMedida unidadeMedida = materialImpNotaSalaUn.getUnidadeMedida();

		// Se o material for diferente de nulo e Unidade Impressão for nulo (UMD_CODIGO)
		if (material != null && unidadeMedida == null) {

			SceConversaoUnidadeConsumos conversaoUnidadeConsumos = getMbcMaterialImpNotaSalaUnRN().obterConversaoUnidadePorMaterial(material);

			if (conversaoUnidadeConsumos != null) {
				// Se ENCONTROU registro seta a unidade de medida do resultado da consulta
				materialImpNotaSalaUn.setUnidadeMedida(conversaoUnidadeConsumos.getUnidadeMedida());
			} else {
				// Se NÃO encontrou registro seta a unidade de medida do material
				materialImpNotaSalaUn.setUnidadeMedida(material.getUnidadeMedida());
			}

		}

	}

	/**
	 * Formata o nome da impressão. O tamanho máximo do campo é 20 carácteres
	 * 
	 * @param descricao
	 * @return
	 */
	private String formatarNomeImpressaoDescricaoMaterial(String descricao) {
		if (descricao != null && descricao.length() > 20) {
			return descricao.substring(0, 20);
		}
		return descricao == null ? "" : descricao;
	}
	
	/**
	 * Pesquisa da SuggestionBox de material
	 * 
	 * @param listaGmtCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ScoMaterial> pesquisarMateriaisAtivosGrupoMaterial(Object objPesquisa) throws ApplicationBusinessException {

		List<Integer> listaGmtCodigo = montaListaGmtCodigo();

		return this.getComprasFacade().pesquisarMateriaisAtivosGrupoMaterial(objPesquisa, listaGmtCodigo);
	}

	private List<Integer> montaListaGmtCodigo()
			throws ApplicationBusinessException {
		List<Integer> listaGmtCodigo = new ArrayList<Integer>();

		// Parâmetro GRPO_MAT_ORT_PROT
		final AghParametros parametroGrpoMatOrtProt = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
		listaGmtCodigo.add(parametroGrpoMatOrtProt.getVlrNumerico().intValue());

		// Parâmetro P_GRPO_MAT_MEDICO_HOSP
		final AghParametros parametroPgrpoMatMedicoHosp = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRPO_MAT_MEDICO_HOSP);
		listaGmtCodigo.add(parametroPgrpoMatMedicoHosp.getVlrNumerico().intValue());

		// Parâmetro P_GR_MAT_MEDIC
		final AghParametros parametroPgrMatMedic = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC);
		listaGmtCodigo.add(parametroPgrMatMedic.getVlrNumerico().intValue());

		// Parâmetro P_GRPO_INSTR_CIRG
		final AghParametros parametroPgrpoInstrCirg = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRPO_INSTR_CIRG);
		listaGmtCodigo.add(parametroPgrpoInstrCirg.getVlrNumerico().intValue());

		// Parâmetro P_GR_FARM_INDUSTRIAL
		final AghParametros parametroPgrFarmIndustrial = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_FARM_INDUSTRIAL);
		listaGmtCodigo.add(parametroPgrFarmIndustrial.getVlrNumerico().intValue());

		// Parâmetro P_GR_HIGIENE_LIMPEZA
		final AghParametros parametroPgrHigieneLimpeza = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_HIGIENE_LIMPEZA);
		listaGmtCodigo.add(parametroPgrHigieneLimpeza.getVlrNumerico().intValue());
		return listaGmtCodigo;
	}
	
	
	/**
	 * Pesquisa CadastroMateriaisImpressaoNotaSalaVO através da unidade de nota sala
	 * @param unidadeNotaSala
	 * @return
	 */
	public List<CadastroMateriaisImpressaoNotaSalaVO> pesquisarCadastroMateriaisImpressaoNotaSala(MbcUnidadeNotaSala unidadeNotaSala) {
		
		List<MbcMaterialImpNotaSalaUn> listaMateriaisNotaSala =  getMbcMaterialImpNotaSalaUnDAO().pesquisarMaterialImpNotaSalaUnPorUnidadeNotaSala(unidadeNotaSala);
		List<CadastroMateriaisImpressaoNotaSalaVO> resultados = new LinkedList<CadastroMateriaisImpressaoNotaSalaVO>();
		
		for (MbcMaterialImpNotaSalaUn item : listaMateriaisNotaSala) {
			
			CadastroMateriaisImpressaoNotaSalaVO vo = new CadastroMateriaisImpressaoNotaSalaVO();
			
			// Código
			vo.setCodigo(item.getSeq());
			
			// Material
			final ScoMaterial material = item.getMaterial();
			vo.setCodigoMaterial(material.getCodigo());
			
			// Material descrição
			String materialDescricao = material != null ? material.getCodigoENome() : "";
			vo.setMaterialDescricao(materialDescricao);
			
			// Unidade de medida (da unidade de medida do material)
			String unidadeMedidaDescricao = material != null  ? material.getUnidadeMedida().getCodigo() : "";
			vo.setUnidadeMedidaDescricao(unidadeMedidaDescricao);
			
			// Nome impressão
			vo.setNomeImpressao(item.getNomeImpressao());
			
			// Unidade de medida
			final ScoUnidadeMedida unidadeMedida = item.getUnidadeMedida();
			vo.setCodigoUnidadeMedida(unidadeMedida.getCodigo());
			
			// Unidade de impressão (da unidade de medida)
			String unidadeMedidaImpressao = unidadeMedida != null ? unidadeMedida.getCodigo() : "";
			vo.setUnidadeImpressao(unidadeMedidaImpressao);
			
			// Ordem impressão
			vo.setOrdemImpressao(item.getOrdemImpressao());
			
			resultados.add(vo);
			
		}
		
		return resultados;
		
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	private MbcMaterialImpNotaSalaUnRN getMbcMaterialImpNotaSalaUnRN() {
		return mbcMaterialImpNotaSalaUnRN;
	}

	private IComprasFacade getComprasFacade() {
		return this.iComprasFacade;
	}

	private IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	private MbcMaterialImpNotaSalaUnDAO getMbcMaterialImpNotaSalaUnDAO() {
		return mbcMaterialImpNotaSalaUnDAO;
	}

	public Long pesquisarMateriaisAtivosGrupoMaterialCount(Object objPesquisa) throws ApplicationBusinessException {
		List<Integer> listaGmtCodigo = montaListaGmtCodigo();

		return this.getComprasFacade().pesquisarMateriaisAtivosGrupoMaterialCount(objPesquisa, listaGmtCodigo);
	}
}
