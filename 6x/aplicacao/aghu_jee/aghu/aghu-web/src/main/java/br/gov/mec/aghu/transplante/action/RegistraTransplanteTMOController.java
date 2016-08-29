package br.gov.mec.aghu.transplante.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;

import br.gov.mec.aghu.model.MtxRegistrosTMO;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RegistraTransplanteTMOController extends ActionController  {

	private static final long serialVersionUID = -3279081396392753333L;
	
	private Integer seqTransplante;
	private String nomePaciente;
	private String prontuario;
	private MtxRegistrosTMO mtxRegistrosTMO;
	private String voltaPara;
	
	private Boolean modoInclusao = Boolean.TRUE;
	private Boolean modoEdicao = Boolean.FALSE;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void iniciar() throws ApplicationBusinessException{
		
		mtxRegistrosTMO = transplanteFacade.obterRegistroTransplantePorTransplante(seqTransplante);
		
		if(mtxRegistrosTMO != null){
			if(mtxRegistrosTMO.getServidor().equals(registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()))){ //RN03
				modoEdicao = Boolean.TRUE;
				modoInclusao = Boolean.FALSE;
			}else{
				modoEdicao = Boolean.FALSE;
				modoInclusao = Boolean.FALSE;
			}
		}else if(mtxRegistrosTMO == null){
			
			mtxRegistrosTMO = new MtxRegistrosTMO();
			mtxRegistrosTMO.setDataHoraRealizacao(new Date());
			
			MtxTransplantes mtxTransplantes = new MtxTransplantes();
			mtxTransplantes.setSeq(seqTransplante);
			
			mtxRegistrosTMO.setMtxTransplante(mtxTransplantes);
			modoInclusao = Boolean.TRUE;
			modoEdicao = Boolean.FALSE;
		}
	}
	
	public String gravar(){
		try {
			
			if(StringUtils.isBlank(mtxRegistrosTMO.getDescricao())){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DESCRICAO_OBRIGATORIA");
				return null;
			}
			
			if(modoInclusao){
				transplanteFacade.persistirMtxRegistrosTMO(mtxRegistrosTMO);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERCAO_REGISTRO_TMO");
			}else if(modoEdicao){
				transplanteFacade.atualizarMtxRegistrosTMO(mtxRegistrosTMO);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_REGISTRO_TMO");
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return voltaPara;
	}
	
	public String voltar(){
		return voltaPara;
	}
	
	public String getVoltaPara() {
		return voltaPara;
	}

	public void setVoltaPara(String voltaPara) {
		this.voltaPara = voltaPara;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public MtxRegistrosTMO getMtxRegistrosTMO() {
		return mtxRegistrosTMO;
	}

	public void setMtxRegistrosTMO(MtxRegistrosTMO mtxRegistrosTMO) {
		this.mtxRegistrosTMO = mtxRegistrosTMO;
	}

	public Boolean getModoInclusao() {
		return modoInclusao;
	}

	public void setModoInclusao(Boolean modoInclusao) {
		this.modoInclusao = modoInclusao;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getSeqTransplante() {
		return seqTransplante;
	}

	public void setSeqTransplante(Integer seqTransplante) {
		this.seqTransplante = seqTransplante;
	}
}
