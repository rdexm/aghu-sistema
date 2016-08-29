package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AbsMotivoRetornoCartas;
import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.AelItemSolicCartasId;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.model.AelRetornoCarta;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class GestaoCartasRecoletaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(GestaoCartasRecoletaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4375862282112364375L;

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private GestaoCartasRecoletaPaginatorController gestaoCartasRecoletaPaginatorController;

	private Short iseSeqp;
	private Integer iseSoeSeq;
	private Short seqp;
	private AipPacientes paciente;
	private AelItemSolicCartas itemSolicitacaoCarta;
	private AelModeloCartas modelo;
	private AelRetornoCarta motivoRetorno;
	
	private List<AelExtratoItemCartas> extrato;

	public enum GestaoCartasRecoletaControllerExceptionCode implements
	BusinessExceptionCode {
		CARTA_RECOLETA_ALTERADA_SUCESSO;
	}

	public void inicio() {
	 

		itemSolicitacaoCarta = this.examesFacade.obterAelItemSolicCartasComPaciente(new AelItemSolicCartasId(iseSeqp, iseSoeSeq, seqp));
		modelo = itemSolicitacaoCarta.getAelModeloCartas();
		motivoRetorno = itemSolicitacaoCarta.getMotivoRetorno();
		if(itemSolicitacaoCarta.getSolicitacaoExame().getAtendimento() != null) {
			paciente = itemSolicitacaoCarta.getSolicitacaoExame().getAtendimento().getPaciente();
		}
		else if(itemSolicitacaoCarta.getSolicitacaoExame().getAtendimentoDiverso() != null) {
			paciente = itemSolicitacaoCarta.getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente();
		}
		
		if(itemSolicitacaoCarta.getAelExtratoItemCartases() != null) {
			this.extrato = this.examesFacade.buscarAelExtratoItemCartasPorItemCartasComMotivoRetorno(itemSolicitacaoCarta);			
			final BeanComparator dthrEventoSorter = new BeanComparator("dthrEvento", new ReverseComparator(new NullComparator(false)));
			Collections.sort(extrato, dthrEventoSorter);
		}

	
	}

	public void emitirCarta() {		
		gestaoCartasRecoletaPaginatorController.emitirCarta(itemSolicitacaoCarta);
	}
	
	public String gravar() {
		try {
			if(motivoRetorno != null) {
				itemSolicitacaoCarta.setMotivoRetorno(motivoRetorno);
			}
			else {
				itemSolicitacaoCarta.setMotivoRetorno(null);
			}
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			itemSolicitacaoCarta.setAelModeloCartas(modelo);
			examesFacade.atualizarAelItemSolicCartas(itemSolicitacaoCarta, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO,
					GestaoCartasRecoletaControllerExceptionCode.CARTA_RECOLETA_ALTERADA_SUCESSO.toString());
			return "gestaoCartasRecoletaList";
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String cancelar() {
		return "gestaoCartasRecoletaList";
	}
	
	public List<AelModeloCartas> listarAelModeloCartasAtivas(final String parametro) {
		return this.examesFacade.listarAelModeloCartasAtivas(parametro);
	}
	
	public List<AbsMotivoRetornoCartas> listarAelRetornoCartaAtivas(String parametro) {
		return this.examesFacade.listarAelRetornoCartaAtivas(parametro);
	}
	
	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AelItemSolicCartas getItemSolicitacaoCarta() {
		return itemSolicitacaoCarta;
	}

	public void setItemSolicitacaoCarta(AelItemSolicCartas itemSolicitacaoCarta) {
		this.itemSolicitacaoCarta = itemSolicitacaoCarta;
	}

	public AelModeloCartas getModelo() {
		return modelo;
	}

	public void setModelo(AelModeloCartas modelo) {
		this.modelo = modelo;
	}

	public AelRetornoCarta getMotivoRetorno() {
		return motivoRetorno;
	}

	public void setMotivoRetorno(AelRetornoCarta motivoRetorno) {
		this.motivoRetorno = motivoRetorno;
	}

	public List<AelExtratoItemCartas> getExtrato() {
		return extrato;
	}

	public void setExtrato(List<AelExtratoItemCartas> extrato) {
		this.extrato = extrato;
	}
}
