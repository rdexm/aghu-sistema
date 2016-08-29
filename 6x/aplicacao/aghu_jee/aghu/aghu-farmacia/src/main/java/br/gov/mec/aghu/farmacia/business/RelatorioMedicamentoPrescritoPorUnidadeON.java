package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoUnidadeVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateFormatUtil;


@Stateless
public class RelatorioMedicamentoPrescritoPorUnidadeON extends
		BaseBusiness implements Serializable {


@EJB
private RelatorioMedicamentoPrescritoPorUnidadeRN relatorioMedicamentoPrescritoPorUnidadeRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4551266640849462139L;

	private static final Log LOG = LogFactory.getLog(RelatorioMedicamentoPrescritoPorUnidadeON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;	
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	/**
	 * Obtem dados para o Relatório Medicamento Prescrito por Unidade
	 * 
	 * @param unidadeFuncional
	 * @param dataDeReferenciaInicio
	 * @param dataDeReferenciaFim
	 * @param medicamento
	 * @param itemDispensado
	 * @param
	 * @return List<MedicamentoPrescritoUnidadeVO>
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public List<MedicamentoPrescritoUnidadeVO> obterConteudoRelatorioMedicamentoPrescritoPorUnidade(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional, AfaMedicamento medicamento,
			Boolean itemDispensado) throws ApplicationBusinessException {
		
		validaLimiteDiasParaRelatorio(dataDeReferenciaInicio, dataDeReferenciaFim);
		
		List<Object[]> listaMedicamento = getAfaDispensacaoMdtosDAO()
				.pesquisarMedicamentoPorDataReferenciaUnidadeMedicamentoItemDispensado(
						dataDeReferenciaInicio, dataDeReferenciaFim,
						unidadeFuncional, medicamento, itemDispensado);
		
		
		List<MedicamentoPrescritoUnidadeVO> listaVOs = new ArrayList<MedicamentoPrescritoUnidadeVO>();
		
		for(int i=0; i < listaMedicamento.size();i++){
			Object[] dispObject = listaMedicamento.get(i);
			//AfaDispensacaoMdtos dispMedicamento = listaMedicamento.get(i);
			
			MedicamentoPrescritoUnidadeVO medicamentoPrescritoUnidadeVO = null;

			medicamentoPrescritoUnidadeVO =
				processaMedicamentoPrescritoUnidadeVO(dataDeReferenciaInicio,
						dataDeReferenciaFim,
						unidadeFuncional, medicamento, itemDispensado, dispObject);

			listaVOs.add(medicamentoPrescritoUnidadeVO);

		}
		
		CoreUtil.ordenarLista(listaVOs, "medicamento", Boolean.FALSE);
		CoreUtil.ordenarLista(listaVOs, "andarAla", Boolean.TRUE);
		
		//return tt;
		
		return listaVOs;
	}

	private void validaLimiteDiasParaRelatorio(Date dataInicio,
			Date dataFim) throws ApplicationBusinessException {
		AghParametros pDifDiasMaximo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_REL_MEDIC_PRESC_UNID);
		Integer difDiasMaximo = pDifDiasMaximo.getVlrNumerico().intValue();
		CoreUtil.validaDifencaLimiteDiasParaRelatorio(dataInicio, dataFim, difDiasMaximo);
	}

	/**
	 * Popula VO para o Relatório Prescrição por Unidade
	 * 
	 * @param unidadeFuncional
	 * @param dataDeReferenciaInicio
	 * @param dataDeReferenciaFim
	 * @param medicamento
	 * @param itemDispensado
	 * @return medicamentoPrescritoUnidadeVO
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private MedicamentoPrescritoUnidadeVO processaMedicamentoPrescritoUnidadeVO(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional, AfaMedicamento medicamento,
			Boolean itemDispensado, Object[] dispObject) throws ApplicationBusinessException {
		
		String descMedicamento = (String) dispObject[0];
		BigDecimal concentracaoMedicamento = dispObject[1] == null? null: (BigDecimal) dispObject[1];
		String unidadeMedidaMedicamento = dispObject[2] != null? (String) dispObject[2]:"";
		String andar = (dispObject[3]).toString(); 
		String sigla = (String) dispObject[4];
		String ala =  (String) dispObject[5];
		String descUnidadeFuncionalSolicitante = (String) dispObject[6];
		String siglaTipoApresentacaoMedicamento = (String) dispObject[7];
		Integer medMatCodigo = (Integer) dispObject[8];
		Date mesAnoDispensacao = (Date) dispObject[9];
		BigDecimal qtdeDispensada = (BigDecimal) dispObject[10];
		BigDecimal qtdeEstornada = dispObject[11] !=null? (BigDecimal) dispObject[11]:BigDecimal.ZERO ; 
		
		String unidadeFuncionalSolicitadaFormatada = formatUnf(andar, sigla, ala, descUnidadeFuncionalSolicitante);
		
		String concentracaoFormatada;
		if (concentracaoMedicamento != null){
			concentracaoFormatada = formatConcentracao(concentracaoMedicamento);
		}else{
			concentracaoFormatada = "";
		}
			
		MedicamentoPrescritoUnidadeVO medicamentoPrescritoUnidadeVO = new MedicamentoPrescritoUnidadeVO();
		
		// Seta o campo de Andar/Ala
		medicamentoPrescritoUnidadeVO.setAndarAla(unidadeFuncionalSolicitadaFormatada);

		// Seta a data de referencia inicial
		medicamentoPrescritoUnidadeVO.setDataReferenciaInicio(DateFormatUtil
				.fomataDiaMesAno(dataDeReferenciaInicio));

		// Seta a data de referencia final
		medicamentoPrescritoUnidadeVO.setDataReferenciaFim(DateFormatUtil
				.fomataDiaMesAno(dataDeReferenciaFim));

		// Formata o campo medicamento e seta 
		medicamentoPrescritoUnidadeVO.setMedicamento(descMedicamento + " " + concentracaoFormatada + " " + unidadeMedidaMedicamento);

		// Seta o valor unitário = custo médio
		BigDecimal valorUnitario =  getRelatorioMedicamentoPrescritoPorUnidadeRN()
		.obterCustoMedioPonderado(converteMedMatCodigoParaScoMaterial(medMatCodigo),
				mesAnoDispensacao);
		
		medicamentoPrescritoUnidadeVO.setValorUnitario(valorUnitario); //String
		
		//Seta a quantidade e o valor total do medicamento
		BigDecimal valorTotal;
						
		BigDecimal quantidade =  qtdeDispensada.subtract(qtdeEstornada);
		valorTotal = (quantidade.multiply(valorUnitario)).setScale(3, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
		medicamentoPrescritoUnidadeVO.setValorTotal(valorTotal);
		
		String quantidadeFormatada = formatString(quantidade, 4);
		
		String quantidadeFormat = quantidadeFormatada + " " + siglaTipoApresentacaoMedicamento;
		medicamentoPrescritoUnidadeVO.setQuantidade(quantidadeFormat);
		
		

		return medicamentoPrescritoUnidadeVO;

	}
	
	public static String formatString(BigDecimal vlr, int scale) {
		
		if(vlr == null){
			vlr = BigDecimal.ZERO ;
		}
		return vlr.setScale(scale, RoundingMode.HALF_DOWN).toString().replace(".", ",").replaceAll("(,0+|0+)$" , "");
		
	}


	private ScoMaterial converteMedMatCodigoParaScoMaterial(Integer medMatCodigo) {
		ScoMaterial scoMaterial = new ScoMaterial();
		
		scoMaterial.setCodigo(medMatCodigo);
		
		return scoMaterial;
	}

	private String formatConcentracao(BigDecimal concentracaoMedicamento) {
		return AghuNumberFormat.formatarValor(
				concentracaoMedicamento, AfaMedicamento.class, "concentracao");
	}

	/**
	 * Método para concatenar andar, sigla, dominio ala, descUnidadeFuncionalSolicitante
	 * @param andar
	 * @param sigla
	 * @param ala
	 * @param descUnidadeFuncionalSolicitante
	 * @return
	 */
	public String formatUnf(String andar, String sigla, String ala,
			String descUnidadeFuncionalSolicitante) {

		StringBuffer unidadeFuncionalSolicitanteFormatada = new StringBuffer();
		
		if ("0".equals(andar) || "1".equals(andar) ) {
			unidadeFuncionalSolicitanteFormatada.append(sigla);
		
		}else{
			unidadeFuncionalSolicitanteFormatada.append(andar).append(' ').append(ala);
		}
		
		unidadeFuncionalSolicitanteFormatada.append(' ').append(descUnidadeFuncionalSolicitante);
		return unidadeFuncionalSolicitanteFormatada.toString();
	}

	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}

	/**
	 * Pesquisa Unidades Funcionais somente pelas características necessárias
	 * para o relatório de prescrição por unidade.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> listarUnidadesPmeInformatizadaByPesquisa(
			Object parametro) {
		List<AghUnidadesFuncionais.Fields> fieldsOrder = Arrays.asList(
				AghUnidadesFuncionais.Fields.ANDAR,
				AghUnidadesFuncionais.Fields.ALA,
				AghUnidadesFuncionais.Fields.DESCRICAO);
		
		return getAghuFacade()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						parametro, null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,
						fieldsOrder,
						ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
	}
	
	/**
	 * Count de Unidades Funcionais somente pelas características necessárias
	 * para o relatório de prescrição por unidade.
	 * 
	 * @param parametro
	 * @return
	 */
	public Long listarUnidadesPmeInformatizadaByPesquisaCount(Object parametro) {

		return getAghuFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				parametro, null, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,
				ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private RelatorioMedicamentoPrescritoPorUnidadeRN getRelatorioMedicamentoPrescritoPorUnidadeRN(){
		return relatorioMedicamentoPrescritoPorUnidadeRN;
	}

}
