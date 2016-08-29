package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class ImprimeTicketController extends ActionReport {

	private static final long serialVersionUID = 1163478101333936914L;	
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private Integer prontuario;
	private String local;
	private String nomePaciente;
	private String prescricaoInicio;
	private String prescricaoFim;
	private Boolean dispencacaoComMdto;
	private Boolean prescricaoEletronica;
	private List<TicketMdtoDispensadoVO> listaMdtoDispensado;
	private Integer qtdVias;
	
	public void iniciarPagina(Integer prontuario, String local,
			String nomePaciente, String prescricaoInicio, String prescricaoFim,
			List<TicketMdtoDispensadoVO> listaMdto, Boolean dispencacaoComMdto,
			Boolean prescricaoEletronica, Integer qtdVias) throws ApplicationBusinessException, JRException, SystemException, IOException {	
		
		try {
			this.prontuario = prontuario;
			this.local = local;
			this.nomePaciente = nomePaciente;
			this.prescricaoInicio = prescricaoInicio;
			this.prescricaoFim = prescricaoFim;
			this.listaMdtoDispensado = listaMdto;
			this.dispencacaoComMdto = dispencacaoComMdto;
			this.prescricaoEletronica = prescricaoEletronica;
			this.qtdVias = qtdVias;
			imprimirTicket();			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}			
	}
	
	
	public void imprimirTicket() throws ApplicationBusinessException, JRException, SystemException, IOException {		
		
		try {
			DocumentoJasper documento = gerarDocumento();
			JasperPrint impressao = documento.getJasperPrint();
			for (int i = 0; i < qtdVias; i++) {
				this.sistemaImpressao.imprimir(impressao,super.getEnderecoIPv4HostRemoto());
			}
			RapServidores servidorLogado;
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			farmaciaDispensacaoFacade.atualizarRegistroImpressao(listaMdtoDispensado, servidorLogado);
			//farmaciaDispensacaoFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MESSAGE_DISP_MDTOS_ETQ_IMPRESSO_SUCESSO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	@Override
	public String recuperarArquivoRelatorio() {
			if(dispencacaoComMdto){
				return "br/gov/mec/aghu/farmacia/report/ticketMdtosDispensados.jasper";
			}else{
				return "br/gov/mec/aghu/farmacia/report/ticket.jasper";
			}
	}


	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();		

		params.put("SUBREPORT_DIR","br/gov/mec/aghu/farmacia/report/");  

		try {
			AghParametros parametroRazaoSocial = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		params.put("prontuario", CoreUtil.formataProntuario(prontuario));
		params.put("localizacaoPaciente", local);
		params.put("nomePaciente", nomePaciente);
		StringBuffer prescricao = new StringBuffer();
		prescricao.append(prescricaoInicio).append(" a ").append(prescricaoFim);		
		params.put("prescricao", prescricao.toString());
		params.put("dataInicioPrescricao", prescricaoInicio);
		params.put("dataFimPrescricao", prescricaoFim);
		params.put("dataFimPrescricao", prescricaoFim);
		params.put("dataAtual", new Date());
		String nomeServidorLogado = "";
		try {
			RapServidores servidorLogado;
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
			nomeServidorLogado = servidorLogado.getPessoaFisica().getNome();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("emitidoPor", nomeServidorLogado);
		String funcionalidade = null;
		if(prescricaoEletronica){
			funcionalidade = "Dispensação de Medicamentos - Prescricao Eletrônica";
		}else{
			funcionalidade = "Dispensação de Medicamentos - Prescricao NÃO Eletrônica";
		}
		params.put("funcionalidade", funcionalidade);
		
		//set outros parametros
		
		return params;
	}
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {			
		if(dispencacaoComMdto){
			CoreUtil.ordenarLista(listaMdtoDispensado, TicketMdtoDispensadoVO.Fields.MDTO_CONTROLADO.toString(), false);
			return listaMdtoDispensado;
		}else{
			return Arrays.asList(new Object());
		}
	}

	// Getters e Setters

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}


	public Integer getProntuario() {
		return prontuario;
	}


	public String getLocal() {
		return local;
	}


	public String getNomePaciente() {
		return nomePaciente;
	}


	public String getPrescricaoInicio() {
		return prescricaoInicio;
	}


	public String getPrescricaoFim() {
		return prescricaoFim;
	}


	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}


	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public void setLocal(String local) {
		this.local = local;
	}


	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}


	public void setPrescricaoInicio(String prescricaoInicio) {
		this.prescricaoInicio = prescricaoInicio;
	}


	public void setPrescricaoFim(String prescricaoFim) {
		this.prescricaoFim = prescricaoFim;
	}


	public Boolean getDispencacaoComMdto() {
		return dispencacaoComMdto;
	}


	public void setDispencacaoComMdto(Boolean dispencacaoComMdto) {
		this.dispencacaoComMdto = dispencacaoComMdto;
	}


	public List<TicketMdtoDispensadoVO> getListaMdtoDispensado() {
		return listaMdtoDispensado;
	}


	public void setListaMdtoDispensado(
			List<TicketMdtoDispensadoVO> listaMdtoDispensado) {
		this.listaMdtoDispensado = listaMdtoDispensado;
	}


	public Boolean getPrescricaoEletronica() {
		return prescricaoEletronica;
	}


	public void setPrescricaoEletronica(Boolean prescricaoEletronica) {
		this.prescricaoEletronica = prescricaoEletronica;
	}


	public void setQtdVias(Integer qtdVias) {
		this.qtdVias = qtdVias;
	}


	public Integer getQtdVias() {
		return qtdVias;
	}

	
}
