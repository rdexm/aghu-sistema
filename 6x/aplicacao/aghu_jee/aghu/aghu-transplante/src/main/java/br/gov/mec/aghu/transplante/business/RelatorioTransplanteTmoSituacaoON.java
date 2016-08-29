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
import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteTmoSituacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioTransplanteTmoSituacaoON extends BaseBusiness {

	private static final long serialVersionUID = -8899288417265871299L;
	
	private static final String ENCODE = "ISO-8859-1";
	private static final String SEPARADOR=";";
	private static final String TRACO = " - ";
	private static final String QUEBRA_LINHA = "\n";
	private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String TITULO_RELATORIO = "Relatório de Pacientes de Transplante de Medula Óssea por Situação";
	private static final String LBL_PACIENTE = "Paciente";
	private static final String LBL_SEXO = "Sexo";
	private static final String LBL_IDADE = "Idade";
	private static final String LBL_CADASTRO = "Cadastro";
	private static final String LBL_PERMANENCIA = "Permanência";
	private static final String LBL_ESCORE = "Escore";
	private static final String LBL_TRANSPLANTE = "Transplante";
	private static final String LBL_INATIVO_EM = "Inativo em";
	private static final String LBL_STAND_BY_EM = "Stand By em";
	private static final String LBL_OBSERVACOES = "Observações:";
	private static final String LBL_STATUS = "Status:";
	private static final String LBL_DIAS = " dias";
	private static final String LBL_TOTAL = "Total: ";
	private static final String LBL_PACIENTES = " pacientes";
	private static final String LBL_RODAPE = "MTXR_SITUACAO_TRANSPLANTE_TMO";
	private Writer out;
	private DominioSituacaoTransplante situacaoTransplanteAnterior;
	private DominioSituacaoTmo situacaoTmoAnterior;
	private DominioTipoAlogenico tipoAlogenicoAnterior;
	private Integer totalPacientes;
	
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum RelatorioTransplanteOrgaoSituacaoONExceptionCode implements BusinessExceptionCode {
		NENHUM_REGISTRO_ENCONTRADO;
	}
	
	public void validarListaRelatorioTransplanteTmoSituacaoVO (List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO) throws ApplicationBusinessException{
		if(listaRelatorioTransplanteTmoSituacaoVO == null || listaRelatorioTransplanteTmoSituacaoVO.isEmpty()){
			throw new ApplicationBusinessException(RelatorioTransplanteOrgaoSituacaoONExceptionCode.NENHUM_REGISTRO_ENCONTRADO, Severity.ERROR);
		}
	}
	
	public void ordenarRelatorioTransplanteTmo(final DominioOrdenacRelatorioSitPacTmo ordenacao, 
			List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioVO, final DominioSituacaoTmo situacaoTmo){
		
		Collections.sort(listaRelatorioVO, new Comparator <RelatorioTransplanteTmoSituacaoVO>() {
	        public int compare(RelatorioTransplanteTmoSituacaoVO o1, RelatorioTransplanteTmoSituacaoVO o2) {
	        	int compareOrdem = o1.getSituacaoTmo().compareTo(o2.getSituacaoTmo());
            	if(compareOrdem == 0 && DominioSituacaoTmo.G.equals(situacaoTmo)){
            		compareOrdem = o1.getTipoAlogenico().compareTo(o2.getTipoAlogenico());
            	}
            	if(compareOrdem == 0){
        			compareOrdem = o1.getOrdemSituacao().compareTo(o2.getOrdemSituacao());
	        		if(compareOrdem == 0){
	        			if(DominioOrdenacRelatorioSitPacTmo.ESCORE.equals(ordenacao)){
	        				return o1.getEscore().compareTo(o2.getEscore());
	        			}else if(DominioOrdenacRelatorioSitPacTmo.NOME.equals(ordenacao)){
	        				return o1.getPacNome().compareTo(o2.getPacNome());
	        			}else if(DominioOrdenacRelatorioSitPacTmo.DATA_INGRESSO.equals(ordenacao)){
	        				return o1.getDataIngresso().compareTo(o2.getDataIngresso());
	        			}
	        		}
            	}
	            return compareOrdem;
	        }
        });
	}

	public String gerarCSV(String nomeHospital, List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO)	throws IOException{
		final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_PACIENTE_TRANSPLANTE_ORGAOS_SITUACAO.getDescricao(), ".cvs");
		out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(nomeHospital+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+DateUtil.dataToString(new Date(), DATE_TIME_PATTERN)+QUEBRA_LINHA);
		out.write(SEPARADOR+SEPARADOR+SEPARADOR+TITULO_RELATORIO+QUEBRA_LINHA);
		out.write(QUEBRA_LINHA);
		escreverDados(listaRelatorioTransplanteTmoSituacaoVO);
		out.flush();
		out.close();
		situacaoTransplanteAnterior = null;
		situacaoTmoAnterior = null;
		tipoAlogenicoAnterior = null;
		out = null;
		totalPacientes = 0;
		return file.getAbsolutePath();
	}
	
	public void escreverDados(List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO) throws IOException{
		situacaoTransplanteAnterior = null;
		situacaoTmoAnterior = null;
		totalPacientes = 0;
		RelatorioTransplanteTmoSituacaoVO item;
		for (int x = 0; x < listaRelatorioTransplanteTmoSituacaoVO.size(); x++) {
			item = listaRelatorioTransplanteTmoSituacaoVO.get(x);
			if(!item.getSituacaoTmo().equals(situacaoTmoAnterior)){
				situacaoTmoAnterior = item.getSituacaoTmo();
				if(DominioSituacaoTmo.U.equals(item.getSituacaoTmo())){
					out.write(item.getSituacaoTmo().getDescricao());
				}else{
					out.write(item.getSituacaoTmo().getDescricao()+TRACO+item.getTipoAlogenico().getDescricao());
					tipoAlogenicoAnterior = item.getTipoAlogenico();
				}
				out.write(QUEBRA_LINHA);
			}else if(DominioSituacaoTmo.G.equals(item.getSituacaoTmo()) && !item.getTipoAlogenico().equals(tipoAlogenicoAnterior)){
				out.write(item.getSituacaoTmo().getDescricao()+TRACO+item.getTipoAlogenico().getDescricao()+QUEBRA_LINHA);
				tipoAlogenicoAnterior = item.getTipoAlogenico();
			}
			criarCabecalho(item, x);
			out.write(item.getPacProntuarioFormatado() + TRACO + item.getPacNome()+SEPARADOR+item.getDescricaoPacSexo());
			out.write(SEPARADOR+item.getIdade()+SEPARADOR);
			if(DominioSituacaoTransplante.A.equals(item.getSituacaoTransplante()) || DominioSituacaoTransplante.E.equals(item.getSituacaoTransplante())){
				out.write(DateUtil.dataToString(item.getDataIngresso(), DATE_PATTERN));
			}else{
				out.write(DateUtil.dataToString(item.getDataOcorrencia(), DATE_PATTERN));
			}
			out.write(SEPARADOR+NumberFormat.getNumberInstance(new Locale("pt","BR")).format(item.getPermanencia())+LBL_DIAS);
			out.write(SEPARADOR+item.getEscore()+QUEBRA_LINHA);
			if(StringUtils.isNotBlank(item.getStatus())){
				out.write(LBL_STATUS+SEPARADOR+item.getStatus()+QUEBRA_LINHA);
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
	}
	
	private void criarCabecalho(RelatorioTransplanteTmoSituacaoVO item, int currentIndex) throws IOException{
		if(!item.getSituacaoTransplante().equals(situacaoTransplanteAnterior)){
			situacaoTransplanteAnterior = item.getSituacaoTransplante();
			if(currentIndex != 0){//se não for a primeira situacao, escreve o total de pacientes da situacao anterior
				out.write(SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+SEPARADOR+LBL_TOTAL+totalPacientes);
				out.write(totalPacientes > 1 ? LBL_PACIENTES : LBL_PACIENTES.replace("s", ""));
				out.write(QUEBRA_LINHA+QUEBRA_LINHA);
			}
			totalPacientes = 0;
			out.write(item.getDescricaoSituacaoTransplante()+QUEBRA_LINHA);
			out.write(LBL_PACIENTE+SEPARADOR+LBL_SEXO+SEPARADOR+LBL_IDADE+SEPARADOR);
			out.write(getLabelSituacao(item.getSituacaoTransplante()));
			out.write(LBL_PERMANENCIA+SEPARADOR);
			out.write(LBL_ESCORE+QUEBRA_LINHA);
		}
	}
	
	private String getLabelSituacao(DominioSituacaoTransplante situacao){
		String str = "";
		if(DominioSituacaoTransplante.A.equals(situacao)){
			str = LBL_CADASTRO+SEPARADOR;
		}else if(DominioSituacaoTransplante.E.equals(situacao)){
			str = LBL_CADASTRO+SEPARADOR;
		}else if(DominioSituacaoTransplante.T.equals(situacao)){
			str = LBL_TRANSPLANTE+SEPARADOR;
		}else if(DominioSituacaoTransplante.I.equals(situacao)){
			str = LBL_INATIVO_EM+SEPARADOR;
		}else if(DominioSituacaoTransplante.S.equals(situacao)){
			str = LBL_STAND_BY_EM+SEPARADOR;
		}
		return str;
	}
	
	public void calcularEscore(List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO){
		int escore;
		for (RelatorioTransplanteTmoSituacaoVO item : listaRelatorioTransplanteTmoSituacaoVO) {
			escore = (int) ((DateUtil.calcularDiasEntreDatas(item.getDataIngresso(), new Date()) * .33) + (item.getCriticidade() + item.getGravidade()));
			if(DateUtil.getIdade(item.getDataNascimento()) < 13){
				item.setEscore(escore + 20);
			}else{
				item.setEscore(escore);
			}
		}
	}
}
