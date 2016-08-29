package br.gov.mec.aghu.transplante.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacOrgao;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteOrgaosSituacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioTransplanteOrgaoSituacaoON extends BaseBusiness {

	private static final long serialVersionUID = -8899288417265871299L;
	
	private static final String ENCODE = "ISO-8859-1";
	private static final String SEPARADOR=";";
	private static final String TRACO = " - ";
	private static final String QUEBRA_LINHA = "\n";
	private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String TITULO_RELATORIO = "Relatório de Pacientes de Transplante de Órgãos por Situação";
	private static final String LBL_PACIENTE = "Paciente";
	private static final String LBL_ORGAO = "Órgão";
	private static final String LBL_SEXO = "Sexo";
	private static final String LBL_IDADE = "Idade";
	private static final String LBL_INGRESSO = "Ingresso";
	private static final String LBL_PERMANENCIA = "Permanência";
	private static final String LBL_TRANSPLANTE = "Transplante";
	private static final String LBL_INATIVO_EM = "Inativo em";
	private static final String LBL_RETIRADO_EM = "Retirado em";
	private static final String LBL_DOENCA_BASE = "Doença Base:";
	private static final String LBL_OBSERVACOES = "Observações:";
	private static final String LBL_DIAS = " dias";
	private static final String LBL_TOTAL = "Total: ";
	private static final String LBL_PACIENTES = " pacientes";
	private static final String LBL_RODAPE = "MTXR_SITUACAO_TRANSPLANTE_ORGAOS";
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum RelatorioTransplanteOrgaoSituacaoONExceptionCode implements BusinessExceptionCode {
		DATA_INVALIDA,
		NENHUM_REGISTRO_ENCONTRADO;
	}
	
	public void validarData(Date dataInicial, Date dataFinal) throws ApplicationBusinessException{
		if((dataInicial != null && dataFinal != null) && dataInicial.after(dataFinal)){
			throw new ApplicationBusinessException(RelatorioTransplanteOrgaoSituacaoONExceptionCode.DATA_INVALIDA, Severity.ERROR);
		}
	}
	
	public void validarListaRelatorioTransplanteOrgaosSituacaoVO (List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO) throws ApplicationBusinessException{
		if(listaRelatorioTransplanteOrgaosSituacaoVO == null || listaRelatorioTransplanteOrgaosSituacaoVO.isEmpty()){
			throw new ApplicationBusinessException(RelatorioTransplanteOrgaoSituacaoONExceptionCode.NENHUM_REGISTRO_ENCONTRADO, Severity.ERROR);
		}
	}
	
	public void ordenarRelatorioTransplanteOrgao(final DominioOrdenacRelatorioSitPacOrgao ordenacao, 
			List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioVO){
		
		Collections.sort(listaRelatorioVO, new Comparator <RelatorioTransplanteOrgaosSituacaoVO>() {
	        public int compare(RelatorioTransplanteOrgaosSituacaoVO o1, RelatorioTransplanteOrgaosSituacaoVO o2) {
	            int compareOrdem = o1.getOrdemSituacao().compareTo(o2.getOrdemSituacao());
	            if (compareOrdem == 0) {
	            	if(DominioOrdenacRelatorioSitPacOrgao.DATA_INGRESSO.equals(ordenacao)){
	            		return compareOrdem = o1.getDataIngresso().compareTo(o2.getDataIngresso());
	            	}else if(DominioOrdenacRelatorioSitPacOrgao.NOME.equals(ordenacao)){
	            		return compareOrdem = o1.getPacNome().compareTo(o2.getPacNome());
	            	}
	            }
	            return compareOrdem;
	        }
        });
	}

	public String gerarCSV(String nomeHospital, List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO) throws IOException{
		final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_PACIENTE_TRANSPLANTE_ORGAOS_SITUACAO.getDescricao(), ".cvs");
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		DominioSituacaoTransplante situacaoTransplanteAnterior = null;
		int totalPacientes = 0;
		RelatorioTransplanteOrgaosSituacaoVO item;
		
		out.write(nomeHospital+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+DateUtil.dataToString(new Date(), DATE_TIME_PATTERN)+QUEBRA_LINHA);
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+TITULO_RELATORIO+QUEBRA_LINHA);
		out.write(QUEBRA_LINHA);
		for (int x = 0; x < listaRelatorioTransplanteOrgaosSituacaoVO.size(); x++) {
			item = listaRelatorioTransplanteOrgaosSituacaoVO.get(x);
			if(!item.getSituacao().equals(situacaoTransplanteAnterior)){
				situacaoTransplanteAnterior = item.getSituacao();
				if(x != 0){//se não for a primeira situacao, escreve o total de pacientes da situacao anterior
					out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+LBL_TOTAL+totalPacientes);
					out.write(totalPacientes > 1 ? LBL_PACIENTES : LBL_PACIENTES.replace("s", ""));
					out.write(QUEBRA_LINHA+QUEBRA_LINHA);
				}
				totalPacientes = 0;
				out.write(item.getDescricaoSituacaoTransplante()+QUEBRA_LINHA);
				out.write(LBL_PACIENTE+SEPARADOR+LBL_ORGAO+SEPARADOR+LBL_SEXO+SEPARADOR+LBL_IDADE+SEPARADOR);
				out.write(getLabelSituacao(item.getSituacao()));
				out.write(LBL_PERMANENCIA+QUEBRA_LINHA);
			}
			out.write(item.getPacProntuarioFormatado() + TRACO + item.getPacNome()+SEPARADOR+item.getDescricaoTipoOrgao()+SEPARADOR+item.getDescricaoPacSexo());
			out.write(SEPARADOR+item.getIdade()+SEPARADOR);
			if(DominioSituacaoTransplante.E.equals(item.getSituacao())){
				out.write(DateUtil.dataToString(item.getDataIngresso(), DATE_PATTERN));
			}else{
				out.write(DateUtil.dataToString(item.getDataOcorrencia(), DATE_PATTERN));
			}
			out.write(SEPARADOR+NumberFormat.getNumberInstance(new Locale("pt","BR")).format(item.getPermanencia())+LBL_DIAS+QUEBRA_LINHA);
			if(StringUtils.isNotBlank(item.getDoencaBase())){
				out.write(LBL_DOENCA_BASE+SEPARADOR+item.getDoencaBase()+QUEBRA_LINHA);
			}
			if(StringUtils.isNotBlank(item.getObservacoes())){
				out.write(LBL_OBSERVACOES+SEPARADOR+item.getObservacoes()+QUEBRA_LINHA);
			}
			totalPacientes++;
			out.write(QUEBRA_LINHA);
		}
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+LBL_TOTAL+totalPacientes);
		out.write(totalPacientes > 1 ? LBL_PACIENTES : LBL_PACIENTES.replace("s", ""));
		out.write(QUEBRA_LINHA+QUEBRA_LINHA+LBL_RODAPE);
		out.flush();
		out.close();
		return file.getAbsolutePath();
	}
	
	private String getLabelSituacao(DominioSituacaoTransplante situacao){
		String str = "";
		if(DominioSituacaoTransplante.E.equals(situacao)){
			str = LBL_INGRESSO+SEPARADOR;
		}else if(DominioSituacaoTransplante.T.equals(situacao)){
			str = LBL_TRANSPLANTE+SEPARADOR;
		}else if(DominioSituacaoTransplante.I.equals(situacao)){
			str = LBL_INATIVO_EM+SEPARADOR;
		}else if(DominioSituacaoTransplante.R.equals(situacao)){
			str = LBL_RETIRADO_EM+SEPARADOR;
		}
		return str;
	}
}
