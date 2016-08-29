package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoEstornadoMotivoVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateFormatUtil;


@Stateless
public class RelatorioMedicamentoEstornadoPorMotivoON extends BaseBusiness implements Serializable{


	@EJB
	private RelatorioMedicamentoPrescritoPorUnidadeRN relatorioMedicamentoPrescritoPorUnidadeRN;
	
	@EJB
	private RelatorioMedicamentoPrescritoPorUnidadeON relatorioMedicamentoPrescritoPorUnidadeON;
	
	private static final Log LOG = LogFactory.getLog(RelatorioMedicamentoEstornadoPorMotivoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -6691574845203429815L;

	/**
	 * Obtem dados para o Relatório de Medicamento Estornado por Motivo
	 * 
	 * @param unidadeFuncional
	 * @param dataDeReferenciaInicio
	 * @param dataDeReferenciaFim
	 * @param tipoOcorDispensacao
	 * @param medicamento
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<MedicamentoEstornadoMotivoVO> obterConteudoRelatorioMedicamentoEstornadoPorMotivo(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim, AghUnidadesFuncionais unidadeFuncional, 
			AfaTipoOcorDispensacao tipoOcorDispensacao, AfaMedicamento medicamento) throws ApplicationBusinessException {
		
		validaLimiteDiasParaRelatorio(dataDeReferenciaInicio, dataDeReferenciaFim);
		
		List<Object[]> listaAfaDispensacaoMdtos = getAfaDispensacaoMdtosDAO().pesquisarMedicamentoEstornadoPorMotivo(dataDeReferenciaInicio, dataDeReferenciaFim, unidadeFuncional, tipoOcorDispensacao, medicamento);
		
		// Cria lista de VOs vazia
		List<MedicamentoEstornadoMotivoVO> listaMedicamentoEstornadoMotivo = new ArrayList<MedicamentoEstornadoMotivoVO>();
		
		for(int ponteiro =0; ponteiro < listaAfaDispensacaoMdtos.size();ponteiro++){
			Object[] mdto = listaAfaDispensacaoMdtos.get(ponteiro);
			
			MedicamentoEstornadoMotivoVO medicamentoEstornadoMotivoVO = new MedicamentoEstornadoMotivoVO();
			
			medicamentoEstornadoMotivoVO = processaMedicamentoEstornadoMotivoVO(dataDeReferenciaInicio,
					dataDeReferenciaFim, unidadeFuncional, tipoOcorDispensacao, medicamento, mdto);
			
			listaMedicamentoEstornadoMotivo.add(medicamentoEstornadoMotivoVO);
		
		}
		
		CoreUtil.ordenarLista(listaMedicamentoEstornadoMotivo, "medicamento", Boolean.FALSE);
		CoreUtil.ordenarLista(listaMedicamentoEstornadoMotivo, "andarAla", Boolean.TRUE);
		
		return listaMedicamentoEstornadoMotivo;
	}
	
	private void validaLimiteDiasParaRelatorio(Date dataInicio,
			Date dataFim) throws ApplicationBusinessException {
		AghParametros pDifDiasMaximo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_REL_MEDIC_ESTOR_MTVO);
		Integer difDiasMaximo = pDifDiasMaximo.getVlrNumerico().intValue();
		CoreUtil.validaDifencaLimiteDiasParaRelatorio(dataInicio, dataFim, difDiasMaximo);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private MedicamentoEstornadoMotivoVO processaMedicamentoEstornadoMotivoVO(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AghUnidadesFuncionais unidadeFuncional,
			AfaTipoOcorDispensacao tipoOcorDispensacao,
			AfaMedicamento medicamento, Object[] mdto) throws ApplicationBusinessException {

		String descMedicamento = (String) mdto[0];
		BigDecimal concentracaoMedicamento = mdto[1] == null? null: (BigDecimal) mdto[1];
		String unidadeMedidaMedicamento = !"".equals((String) mdto[2]) ? (String) mdto[2] : "";
		String andar = (String) mdto[3]; 
		String sigla = (String) mdto[4];
		String ala =  (String) mdto[5];
		String descUnidadeFuncionalSolicitante = (String) mdto[6];
		BigDecimal qtdeDispensada = (BigDecimal) mdto[7];
		BigDecimal qtdeEstornada = mdto[8] !=null? (BigDecimal) mdto[8]:BigDecimal.ZERO;
		String siglaTipoApresentacaoMedicamento = mdto[9] != null ? (String) mdto[9] : "";
		Integer medMatCodigo = (Integer) mdto[10];
		String tipoOcorDispensada = (String) mdto[11];
		
		String unidadeFuncionalSolicitadaFormatada = getRelatorioMedicamentoPrescritoPorUnidadeON().formatUnf(andar, sigla, ala, descUnidadeFuncionalSolicitante);
		
		String concentracaoFormatada = StringUtils.EMPTY;
		if (concentracaoMedicamento != null){
			concentracaoFormatada = formatConcentracao(concentracaoMedicamento);
		}
		
		MedicamentoEstornadoMotivoVO medicamentoEstornadoMotivoVO = new MedicamentoEstornadoMotivoVO();
		
		// Seta a data de referencia inicial
		medicamentoEstornadoMotivoVO.setDataDeReferenciaInicio(DateFormatUtil.fomataDiaMesAno(dataDeReferenciaInicio));

		// Seta a data de referencia final
		medicamentoEstornadoMotivoVO.setDataDeReferenciaFim(DateFormatUtil.fomataDiaMesAno(dataDeReferenciaFim));
		
		// Seta o campo de Andar/Ala
		medicamentoEstornadoMotivoVO.setAndarAla(unidadeFuncionalSolicitadaFormatada);
		
		// Seta o codigo do medicamento
		medicamentoEstornadoMotivoVO.setCodigoMedicamento(medMatCodigo);
		
		// Seta descrição do medicamento
		medicamentoEstornadoMotivoVO.setMedicamento(descMedicamento + " " + concentracaoFormatada + " " + (unidadeMedidaMedicamento != null ? unidadeMedidaMedicamento : ""));
		
		// Seta o motivo
		if(tipoOcorDispensada != null && tipoOcorDispensada.length() > 10){
			medicamentoEstornadoMotivoVO.setTipoOcorDispensacao(tipoOcorDispensada.substring(0, 10));
		}else{
			medicamentoEstornadoMotivoVO.setTipoOcorDispensacao(tipoOcorDispensada);
		}
		
		//Seta a quantidade dispensada
		StringBuffer qtdeDispensadaETipoApresentacao = new StringBuffer(qtdeDispensada.setScale(10, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString().replace("\\.0*$", "").replace('.', ','))
															.append(' ') 
															.append(siglaTipoApresentacaoMedicamento);
		medicamentoEstornadoMotivoVO.setQuantidadeDispensada(qtdeDispensadaETipoApresentacao.toString());
		
		//Formata e seta a quantidade estornada
		StringBuffer qtdeEstornadaETipoApresentacao = new StringBuffer(qtdeEstornada.setScale(10, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString().replace("\\.0*$", "").replace('.', ','))
														.append(' ') 
														.append(siglaTipoApresentacaoMedicamento);
		
		medicamentoEstornadoMotivoVO.setQuantidadeEstornada(qtdeEstornadaETipoApresentacao.toString());
		
		//Seta o custo médio
		medicamentoEstornadoMotivoVO.setCustoUnitario(getRelatorioMedicamentoPrescritoPorUnidadeRN()
				.obterCustoMedioPonderado(converteMedMatCodigoParaScoMaterial(medMatCodigo),
						dataDeReferenciaInicio));
		
		//Calcula o valor total e seta
		BigDecimal valorTotal;
		
		valorTotal = qtdeDispensada.multiply(medicamentoEstornadoMotivoVO.getCustoUnitario());
		
		medicamentoEstornadoMotivoVO.setCustoTotal(valorTotal);
		
		return medicamentoEstornadoMotivoVO;
	}

	private RelatorioMedicamentoPrescritoPorUnidadeON getRelatorioMedicamentoPrescritoPorUnidadeON() {
	
	return relatorioMedicamentoPrescritoPorUnidadeON;
}

	private String formatConcentracao(BigDecimal concentracaoMedicamento) {
		return AghuNumberFormat.formatarValor( concentracaoMedicamento, AfaMedicamento.class, "concentracao");
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	private RelatorioMedicamentoPrescritoPorUnidadeRN getRelatorioMedicamentoPrescritoPorUnidadeRN(){
		return relatorioMedicamentoPrescritoPorUnidadeRN;
	}
	
	private ScoMaterial converteMedMatCodigoParaScoMaterial(Integer medMatCodigo) {
		ScoMaterial scoMaterial = new ScoMaterial();
		
		scoMaterial.setCodigo(medMatCodigo);
		
		return scoMaterial;
	}
	
}