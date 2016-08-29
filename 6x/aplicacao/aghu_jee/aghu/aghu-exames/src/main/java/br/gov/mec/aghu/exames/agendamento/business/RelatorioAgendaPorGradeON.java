package br.gov.mec.aghu.exames.agendamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorGradeVO;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioAgendaPorGradeON extends BaseBusiness implements Serializable {

private static final Log LOG = LogFactory.getLog(RelatorioAgendaPorGradeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5932661676766248094L;
	private static final String SEPARADOR = ";";
	private static final String ENCODE = "ISO-8859-1";
	private static final String PREFIXO = "AGDA_";	
	private static final String EXTENSAO = ".csv";
	
	private static final Integer MAXIMO_DIAS_GERACAO_RELATORIO = 31;
	
	public enum AgendaPorGradeONExceptionCode implements BusinessExceptionCode {
		ERRO_IMPRIMIR_AGENDAS_GRADE_DATA_INICIAL_MAIOR_QUE_FINAL, ERRO_IMPRIMIR_AGENDAS_GRADE_GRADE_NAO_INFORMADA,
		ERRO_IMPRIMIR_AGENDAS_GRADE_PERIODO_EXCEDE_NUM_MAXIMO_DIAS;
	}
	
	public String gerarArquivoAgendas(List<RelatorioAgendaPorGradeVO> listaRelatorioAgendaPorGradeVO) throws IOException{
		File file = File.createTempFile(PREFIXO, EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(gerarCabecalho());
		for (RelatorioAgendaPorGradeVO relAgendaPorGradeVO : listaRelatorioAgendaPorGradeVO){
			out.write(System.getProperty("line.separator"));			
			out.write(gerarLinhaArquivo(relAgendaPorGradeVO));
		}
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	
	public String gerarCabecalho() {
		return "Código" + SEPARADOR + "Unidade" + SEPARADOR + "Grade" + SEPARADOR + "Sala" + SEPARADOR + "Grupo" + SEPARADOR
				+ "Data/Hora Agenda" + SEPARADOR + "Sit" + SEPARADOR
				+ "Prontuário" + SEPARADOR + "Nome do Paciente" + SEPARADOR + "Nº Solicitação"
				+ SEPARADOR + "Exames";
	}	
	
	
	public String gerarLinhaArquivo(RelatorioAgendaPorGradeVO relAgendaPorGradeVO){
		StringBuilder texto = new StringBuilder();
		SimpleDateFormat ddMMyyHHmm=new SimpleDateFormat("dd/MM/yyyy HH:mm");
		AghUnidadesFuncionais unidade = getAghuFacade()
				.obterAghUnidadesFuncionaisPorChavePrimaria(relAgendaPorGradeVO.getGaeUnfSeq());
		
		adicionarTexto(relAgendaPorGradeVO.getGaeUnfSeq(), texto);
		adicionarTexto(unidade.getDescricao(), texto);	
		adicionarTexto(relAgendaPorGradeVO.getGaeSeqp(), texto);
		adicionarTexto(relAgendaPorGradeVO.getNumeroSala(), texto);	
		adicionarTexto(relAgendaPorGradeVO.getDescricaoGrupoExames(), texto);
		adicionarTexto(ddMMyyHHmm.format(relAgendaPorGradeVO.getDthrAgenda()), texto);
		adicionarTexto(relAgendaPorGradeVO.getSituacaoHorario().getDescricao(), texto);	
		adicionarTexto(relAgendaPorGradeVO.getProntuario(), texto);
		adicionarTexto(relAgendaPorGradeVO.getPacNome(), texto);
		adicionarTexto(relAgendaPorGradeVO.getSoeSeq(), texto);
		adicionarTexto(relAgendaPorGradeVO.getDescricaoExame(), texto);
		
		return texto.toString();
	}

	
	private void adicionarTexto(Object texto, StringBuilder sb){
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(SEPARADOR);
	}
	
	
	public void validarFiltrosRelatorioAgendas(Date dataInicial, Date dataFinal, AelGradeAgendaExame gradeAgendaExame) 
			throws ApplicationBusinessException {
	
		validarPeriodoRelatorioAgendas(dataInicial, dataFinal);
		
		if (gradeAgendaExame == null) {
			throw new ApplicationBusinessException(AgendaPorGradeONExceptionCode.ERRO_IMPRIMIR_AGENDAS_GRADE_GRADE_NAO_INFORMADA);
		}
	}
	 	
	public void validarPeriodoRelatorioAgendas(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		if (DateUtil.validaDataMaior(dataInicial, dataFinal)) { 
			throw new ApplicationBusinessException(
					AgendaPorGradeONExceptionCode.ERRO_IMPRIMIR_AGENDAS_GRADE_DATA_INICIAL_MAIOR_QUE_FINAL);
		}
		
		if (DateUtil.calcularDiasEntreDatas(dataInicial, dataFinal) > MAXIMO_DIAS_GERACAO_RELATORIO) {
			throw new ApplicationBusinessException(
					AgendaPorGradeONExceptionCode.ERRO_IMPRIMIR_AGENDAS_GRADE_PERIODO_EXCEDE_NUM_MAXIMO_DIAS);
		}
	}
	
	/**
	 * Retorna lista de SEQ de Solicitações de Exame das Agendas
	 * 
	 * @return
	 */
	public List<Integer> obterListaSoeSeqGradeAgenda(List<RelatorioAgendaPorGradeVO> listaRelatorioAgendaPorGradeVO) {
		List<Integer> listaSoeSeq = new ArrayList<Integer>();
		
		for (RelatorioAgendaPorGradeVO agenda : listaRelatorioAgendaPorGradeVO) {
			Integer soeSeq = agenda.getSoeSeq();
			if (agenda.getSoeSeq() != null) {
				if (!listaSoeSeq.contains(soeSeq)) {
					listaSoeSeq.add(soeSeq);
				}
			}
		}
		return listaSoeSeq;
	}
	
	public List<RelatorioAgendaPorGradeVO> obterAgendasPorGrade(Short gaeUnfSeq, Integer gaeSeqp,
			Date dthrAgendaInicial, Date dthrAgendaFinal, Boolean impHorariosLivres, Boolean isPdf) {
		
		List<RelatorioAgendaPorGradeVO> listRelatorioAgendaVO = getAelGradeAgendaExameDAO()
			.obterAgendasPorGrade(gaeUnfSeq, gaeSeqp, dthrAgendaInicial, dthrAgendaFinal, impHorariosLivres);
		//Refatora lista para atender o subrelatorio caso seja chamado o PDF e não o csv.
		if(isPdf) {
			Set<RelatorioAgendaPorGradeVO> hashSet = new HashSet<RelatorioAgendaPorGradeVO>(listRelatorioAgendaVO);
			for(RelatorioAgendaPorGradeVO item : hashSet) {
				List<RelatorioAgendaPorGradeVO> subReport = new ArrayList<RelatorioAgendaPorGradeVO>();
				for(RelatorioAgendaPorGradeVO sub : listRelatorioAgendaVO) {
					if(item.equals(sub)) {
						subReport.add(sub);
					}
				}
				item.setSubReport(subReport);
			}
			listRelatorioAgendaVO = new ArrayList<RelatorioAgendaPorGradeVO>(hashSet);
			
			class OrdenacaoRelatorio implements Comparator<RelatorioAgendaPorGradeVO> {
				@Override
				public int compare(RelatorioAgendaPorGradeVO o1, RelatorioAgendaPorGradeVO o2) {
					if (o1.getDthrAgenda().compareTo(o2.getDthrAgenda()) != 0) {
						return o1.getDthrAgenda().compareTo(o2.getDthrAgenda());
					} else {
						return 0;
					}
				}
			}
			Collections.sort(listRelatorioAgendaVO, new OrdenacaoRelatorio());
		}
		return listRelatorioAgendaVO;
	}
	
	protected IAghuFacade getAghuFacade(){
		return aghuFacade;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO() {
		return aelGradeAgendaExameDAO;
	}
	
}