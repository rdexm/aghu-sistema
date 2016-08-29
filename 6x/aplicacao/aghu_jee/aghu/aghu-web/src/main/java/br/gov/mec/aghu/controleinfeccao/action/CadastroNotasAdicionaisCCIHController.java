package br.gov.mec.aghu.controleinfeccao.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciNotasCCIH;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class CadastroNotasAdicionaisCCIHController extends ActionController {
	
	private static final Log LOG = LogFactory.getLog(CadastroNotasAdicionaisCCIHController.class);

	private static final long serialVersionUID = -8379847437301077083L;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private String voltarPara;
	private Integer pacCodigo;

	private AipPacientes paciente;
	private String idade;
	private String prontuarioFormatado;
	private MciNotasCCIH notaCCIH;
	private List<MciNotasCCIH> listaNotas;
	private MciNotasCCIH notaCCIHSelecionada;
	private Boolean modoEdicao = Boolean.FALSE;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		this.recarregarTela();
	}
	
	public void recarregarTela() {
		this.limpar();
		this.carregarDadosPaciente();
		this.obterNotasCCIH();
	}
	
	public void carregarDadosPaciente() {
		this.paciente = this.pacienteFacade.obterPaciente(pacCodigo);
		if (paciente != null) {
			prontuarioFormatado = CoreUtil.formataProntuario(paciente.getProntuario());
			Integer idade = DateUtil.getIdade(paciente.getDtNascimento());
			this.idade = idade.toString().concat(" anos"); 
			
			notaCCIH.setPaciente(paciente);
		}
	}
	
	public void obterNotasCCIH() {
		this.listaNotas = this.controleInfeccaoFacade.bucarNotasCCIHPorPacCodigo(pacCodigo);
	}
	
	public void gravarNota() {
		try {
			
			this.controleInfeccaoFacade.persistirMciNotasCCIH(notaCCIH);
			
			if (this.modoEdicao) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_CCIH_SUCESSO_ALTERACAO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_CCIH_SUCESSO_CADASTRO");				
			}
			this.recarregarTela();
		} catch (ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			if (this.modoEdicao) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOTA_CCIH_ERRO_ALTERACAO");
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOTA_CCIH_ERRO_CADASTRO");
			}
		}
	}
	
	public void alterarNota() {
		this.gravarNota();
	}
	
	public void excluir() {
		try {
			this.controleInfeccaoFacade.excluirMciNotasCCIH(notaCCIHSelecionada.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_CCIH_SUCESSO_EXCLUSAO");
			recarregarTela();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NOTA_CCIH_ERRO_EXCLUSAO");
		}
	}
	
	public void editar(MciNotasCCIH notaCCIH) {
		this.modoEdicao = Boolean.TRUE;
		this.notaCCIH = notaCCIH;		
	}
	
	public void cancelarEdicao() {
		recarregarTela();
	}
	
	public String voltar() {
		String retorno = this.voltarPara;
		this.voltarPara = null;
		this.pacCodigo = null;
		this.paciente = null;
		this.idade = null;
		this.prontuarioFormatado = null;
		
		this.limpar();
		
		return retorno;
	}
	
	private void limpar() {
		listaNotas = null;
		notaCCIHSelecionada = null;
		modoEdicao = Boolean.FALSE;

		this.notaCCIH = new MciNotasCCIH();
		this.notaCCIH.setDtHrInicio(new Date());	
	}
	
	
	//GETTERS AND SETTERS
	public String getVoltarPara() {
		return voltarPara;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}
	
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public MciNotasCCIH getNotaCCIH() {
		return notaCCIH;
	}
	
	public void setNotaCCIH(MciNotasCCIH notaCCIH) {
		this.notaCCIH = notaCCIH;
	}
	
	public List<MciNotasCCIH> getListaNotas() {
		return listaNotas;
	}
	
	public void setListaNotas(List<MciNotasCCIH> listaNotas) {
		this.listaNotas = listaNotas;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public MciNotasCCIH getNotaCCIHSelecionada() {
		return notaCCIHSelecionada;
	}

	public void setNotaCCIHSelecionada(MciNotasCCIH notaCCIHSelecionada) {
		this.notaCCIHSelecionada = notaCCIHSelecionada;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
}
