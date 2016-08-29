package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
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
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoPacienteVO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtosPai;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioMedicamentosPrescritosPorPacienteON extends BaseBusiness implements Serializable{

	private static final Log LOG = LogFactory.getLog(RelatorioMedicamentosPrescritosPorPacienteON.class);
	
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
	
	
	public enum RelatorioMedicamentosPrescritosPorPacienteONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_LIMITE_DIAS_RELATORIO
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7488491324362269963L;

	/**
	 * Obtem dados para o Relatório Prescrição por Paciente
	 * 
	 * @param unidadeFuncional
	 * @param dataDeReferenciaInicio
	 * @param dataDeReferenciaFim
	 * @param medicamento
	 * @param 
	 * @return retornoRelatorio
	 * @throws ApplicationBusinessException
	 */
	public List<MedicamentoPrescritoPacienteVO> obterConteudoRelatorioMedicamentosPrescritosPorPaciente(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim, AghUnidadesFuncionais unidadeFuncionalSolicitante, 
			AfaMedicamento medicamento, AfaGrupoUsoMedicamento grupo, AfaTipoUsoMdto tipo, Boolean itemDispensado, Integer pacCodigo) throws ApplicationBusinessException {
		
		List<MedicamentoPrescritoPacienteVO> retornoRelatorio = new ArrayList<MedicamentoPrescritoPacienteVO>();
		
		//Tratamento para campos do tipo Date
		dataDeReferenciaInicio = DateUtil.truncaData(dataDeReferenciaInicio);
		dataDeReferenciaFim = DateUtil.truncaData(dataDeReferenciaFim);
		
		AghParametros difMaxDiasRelatorio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_REL_MDTO_PRESC_PAC);
		if(DateUtil.calcularDiasEntreDatas(dataDeReferenciaInicio, dataDeReferenciaFim) > difMaxDiasRelatorio.getVlrNumerico().intValue()){
			throw new ApplicationBusinessException(
					RelatorioMedicamentosPrescritosPorPacienteONExceptionCode.MENSAGEM_LIMITE_DIAS_RELATORIO
					,difMaxDiasRelatorio.getVlrNumerico().intValue()
					,DateUtil.dataToString(dataDeReferenciaInicio, DateConstants.DATE_PATTERN_DDMMYYYY)
					,DateUtil.dataToString(dataDeReferenciaFim, DateConstants.DATE_PATTERN_DDMMYYYY)
					,DateUtil.calcularDiasEntreDatas(dataDeReferenciaInicio, dataDeReferenciaFim));
		}
		
		List<Object[]> dispensacaoMdtoList = getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtosPacientePorPeriodo(dataDeReferenciaInicio, dataDeReferenciaFim,
				itemDispensado,unidadeFuncionalSolicitante,medicamento,grupo,tipo, pacCodigo);
		
		List<Object[]> dispensacaoMdtoTriagemList = new ArrayList<Object[]>();
		if(!itemDispensado){
			dispensacaoMdtoTriagemList = getAfaDispensacaoMdtosDAO().pesquisarDispensacaoMdtosTriagemPacientePorPeriodo(
				dataDeReferenciaInicio, dataDeReferenciaFim,unidadeFuncionalSolicitante,medicamento,grupo,tipo, pacCodigo);
		}
		
		if (dispensacaoMdtoList != null && dispensacaoMdtoList.size() > 0) {
			retornoRelatorio.addAll(transformaResultados(dispensacaoMdtoList,dataDeReferenciaInicio,dataDeReferenciaFim));
		}
		if (dispensacaoMdtoTriagemList != null && dispensacaoMdtoTriagemList.size() > 0) {
			retornoRelatorio.addAll(transformaResultados(dispensacaoMdtoTriagemList,dataDeReferenciaInicio,dataDeReferenciaFim));
		}
		
		if (retornoRelatorio.size() > 0) {
			CoreUtil.ordenarLista(retornoRelatorio, "prontuarioPacienteInt", true);
			CoreUtil.ordenarLista(retornoRelatorio, "indSituacao", false);
			CoreUtil.ordenarLista(retornoRelatorio, "medicamentoDescricaoCompleta", true);
		}
		
		return retornoRelatorio;
	}
	
	private IParametroFacade getParametroFacade(){
		return parametroFacade;
	}
	
	/**
	 * Pesquisa Unidades Funcionais somente pelas características necessárias para
	 * o relatório de prescrição por paciente.
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> listarUnidadesPrescricaoByPesquisa(
			Object parametro) {

		return getAghuFacade()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						parametro,
						null,
						Boolean.TRUE,
						Boolean.TRUE,
						Boolean.TRUE,
						Arrays.asList(AghUnidadesFuncionais.Fields.ANDAR,
								AghUnidadesFuncionais.Fields.ALA,
								AghUnidadesFuncionais.Fields.DESCRICAO),
						ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
	}
	
	/**
	 * Count de Unidades Funcionais somente pelas características necessárias para
	 * o relatório de prescrição por paciente.
	 * @param parametro
	 * @return
	 */
	public Long listarUnidadesPrescricaoByPesquisaCount(
			Object parametro) {

		return getAghuFacade()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
						parametro,
						null,
						Boolean.TRUE,
						Boolean.TRUE,
						Boolean.TRUE,
						ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}

	
	//UTIL METHODS
	
	private List<MedicamentoPrescritoPacienteVO> transformaResultados(List<Object[]> lista,Date dataDeReferenciaInicio, Date dataDeReferenciaFim) {
		
		List<MedicamentoPrescritoPacienteVO> resultadoTransformado = null;
		
		if (lista != null && lista.size() > 0) {
			resultadoTransformado = new ArrayList<MedicamentoPrescritoPacienteVO>();
			for (Object[] objeto : lista) {
				MedicamentoPrescritoPacienteVO medicamentosPrescritosPacienteVO = transformaMedicamentosPrescritosPacienteVO(objeto, dataDeReferenciaInicio, dataDeReferenciaFim);
				resultadoTransformado.add(medicamentosPrescritosPacienteVO);
			}
		}
		
		return resultadoTransformado;
	}
	
	/**
	 * #5713
	 * Converte resultado da consulta para Visual Object de relatorio
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private MedicamentoPrescritoPacienteVO transformaMedicamentosPrescritosPacienteVO(Object[] objeto,Date dataDeReferenciaInicio, Date dataDeReferenciaFim) {
		
		MedicamentoPrescritoPacienteVO medicamentosPrescritosPacienteVO = new MedicamentoPrescritoPacienteVO();
		
		medicamentosPrescritosPacienteVO.setDataReferenciaInicio(DateUtil.dataToString(dataDeReferenciaInicio, DateConstants.DATE_PATTERN_DDMMYYYY));
		medicamentosPrescritosPacienteVO.setDataReferenciaFim(DateUtil.dataToString(dataDeReferenciaFim, DateConstants.DATE_PATTERN_DDMMYYYY));
		
		String mdtoDescricao = null;
		if (objeto[0] != null) {
			mdtoDescricao = objeto[0].toString();
		}
		String mdtoConcentracao = null;
		if (objeto[1] != null) {
			mdtoConcentracao = objeto[1].toString();
		}
		String mdtoTprSigla = null;
		if (objeto[2] != null) {
			mdtoTprSigla = objeto[2].toString();
		}
		
		String ummDescricao = null;
		if (objeto[3] != null) {
			ummDescricao = objeto[3].toString();
		}
		medicamentosPrescritosPacienteVO.setMedicamentoDescricaoCompleta(mdtoDescricao,mdtoConcentracao,ummDescricao);
		
		String unfAndar = null;
		if (objeto[4] != null) {
			unfAndar = objeto[4].toString();
		}
		String unfAla = null;
		if (objeto[5] != null) {
			unfAla = objeto[5].toString();
		}
		String unfDescricao = null;
		if( objeto[6] != null) {
			unfDescricao = objeto[6].toString();
		}
		String unfSigla = null;
		if (objeto[7] != null) {
			unfSigla = objeto[7].toString();
		}
		medicamentosPrescritosPacienteVO.setUnfAndarAlaDescricaoSigla(unfAndar, unfAla, unfDescricao, unfSigla);
		
		String pacienteNome = null;
		if (objeto[8] != null) {
			pacienteNome = objeto[8].toString();
		}
		String prontuario = null;
		if (objeto[9] != null) {
			prontuario = objeto[9].toString();
		}
		DominioSituacaoDispensacaoMdto indSituacao = null;
		if(objeto[10] != null) {
			indSituacao = (DominioSituacaoDispensacaoMdto) objeto[10];
		}
		BigDecimal qtdeDispensada = BigDecimal.ZERO;
		if(objeto[11] != null) {
			qtdeDispensada = (BigDecimal) objeto[11];
		}
		BigDecimal qtdeEstornada = BigDecimal.ZERO;
		if(objeto[12] != null) {
			qtdeEstornada = (BigDecimal) objeto[12];
		}
		
		qtdeDispensada = qtdeDispensada.subtract(qtdeEstornada);
		
		String qtdeDispensadaFormatada = qtdeDispensada.toString();
		
		qtdeDispensadaFormatada = AghuNumberFormat.formatarValor(
					qtdeDispensada, AfaDispensacaoMdtosPai.class, "qtdeDispensada");
		
		String qtdeDispensadaStr = qtdeDispensadaFormatada + " " + mdtoTprSigla;
		
		medicamentosPrescritosPacienteVO.setQtdeDispensada(qtdeDispensadaStr);
		medicamentosPrescritosPacienteVO.setQtdeDispensada1(qtdeDispensada);
		medicamentosPrescritosPacienteVO.setNomePaciente(pacienteNome);
		medicamentosPrescritosPacienteVO.setProntuarioPaciente(prontuario);
		medicamentosPrescritosPacienteVO.setIndSituacao(indSituacao.name());
		
		return medicamentosPrescritosPacienteVO;
	}
	
}