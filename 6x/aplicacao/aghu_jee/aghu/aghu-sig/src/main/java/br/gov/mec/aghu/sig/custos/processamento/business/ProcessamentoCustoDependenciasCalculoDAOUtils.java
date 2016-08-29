package br.gov.mec.aghu.sig.custos.processamento.business;

import javax.inject.Inject;

import br.gov.mec.aghu.sig.dao.SigAtividadePessoaRestricoesDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdCIDSDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdConsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPacienteDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdPermanenciaDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtividadeEquipamentoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtividadeInsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtividadePessoaDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoAtividadeServicoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoClienteDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoComponenteDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoDetalheConsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoIndiretoEquipamentoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoIndiretoInsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoIndiretoPessoaDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoIndiretoServicoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoObjetoCustoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoRateioEquipamentoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoRateioInsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoRateioPessoaDAO;
import br.gov.mec.aghu.sig.dao.SigCalculoRateioServicoDAO;

public class ProcessamentoCustoDependenciasCalculoDAOUtils extends ProcessamentoCustoDependenciasFacadeUtils {

	private static final long serialVersionUID = -5173541685761206413L;

	@Inject   
	private SigCalculoAtdPermanenciaDAO sigCalculoAtdPermanenciaDAO;
	
	@Inject  
	private SigCalculoIndiretoInsumoDAO sigCalculoIndiretoInsumoDAO;

	@Inject  
	private SigCalculoIndiretoEquipamentoDAO sigCalculoIndiretoEquipamentoDAO;

	@Inject  
	private SigCalculoIndiretoPessoaDAO sigCalculoIndiretoPessoaDAO;
	
	@Inject  
	private SigCalculoIndiretoServicoDAO sigCalculoIndiretoServicoDAO;

	@Inject  
	private SigCalculoClienteDAO sigCalculoClienteDAO;
	
	@Inject 
	private SigCalculoAtdCIDSDAO sigCalculoAtdCIDSDAO;
	
	@Inject   
	private SigCalculoDetalheConsumoDAO sigCalculoDetalheConsumoDAO;
	
	@Inject   
	private SigCalculoAtdConsumoDAO sigCalculoAtdConsumoDAO;
	
	@Inject   
	private SigCalculoObjetoCustoDAO sigCalculoObjetoCustoDAO;

	@Inject   
	private SigCalculoRateioInsumoDAO sigCalculoRateioInsumoDAO;

	@Inject  
	private SigCalculoComponenteDAO sigCalculoComponenteDAO;

	@Inject   
	private SigCalculoAtividadeInsumoDAO sigCalculoAtividadeInsumoDAO;

	@Inject   
	private SigCalculoAtdPacienteDAO sigCalculoAtdPacienteDAO;
	
	@Inject   
	private SigCalculoAtividadeEquipamentoDAO sigCalculoAtividadeEquipamentoDAO;

	@Inject   
	private SigCalculoRateioEquipamentoDAO sigCalculoRateioEquipamentoDAO;

	@Inject   
	private SigCalculoAtividadePessoaDAO sigCalculoAtividadePessoaDAO;

	@Inject   
	private SigCalculoRateioPessoaDAO sigCalculoRateioPessoaDAO;

	@Inject   
	private SigCalculoAtividadeServicoDAO sigCalculoAtividadeServicoDAO;

	@Inject   
	private SigCalculoRateioServicoDAO sigCalculoRateioServicoDAO;

	@Inject
	private SigAtividadePessoaRestricoesDAO sigAtividadePessoaRestricoesDAO;
	
	
	public SigCalculoAtdCIDSDAO getSigCalculoAtdCIDSDAO() {
		return sigCalculoAtdCIDSDAO;
	}
	
	public SigCalculoDetalheConsumoDAO getSigCalculoDetalheConsumoDAO() {
		return sigCalculoDetalheConsumoDAO;
	}
	
	public SigCalculoAtdConsumoDAO getSigCalculoAtdConsumoDAO() {
		return sigCalculoAtdConsumoDAO;
	}
	
	public SigCalculoAtividadeEquipamentoDAO getSigCalculoAtividadeEquipamentoDAO() {
		return sigCalculoAtividadeEquipamentoDAO;
	}

	public SigCalculoRateioEquipamentoDAO getSigCalculoRateioEquipamentoDAO() {
		return sigCalculoRateioEquipamentoDAO;
	}

	public SigCalculoAtividadePessoaDAO getSigCalculoAtividadePessoaDAO() {
		return sigCalculoAtividadePessoaDAO;
	}

	public SigCalculoRateioPessoaDAO getSigCalculoRateioPessoaDAO() {
		return sigCalculoRateioPessoaDAO;
	}

	public SigCalculoAtividadeServicoDAO getSigCalculoAtividadeServicoDAO() {
		return sigCalculoAtividadeServicoDAO;
	}

	public SigCalculoRateioServicoDAO getSigCalculoRateioServicoDAO() {
		return sigCalculoRateioServicoDAO;
	}
	
	public SigCalculoAtdPermanenciaDAO getSigCalculoAtdPermanenciaDAO() {
		return sigCalculoAtdPermanenciaDAO;
	}
	
	public SigCalculoIndiretoInsumoDAO getSigCalculoIndiretoInsumoDAO() {
		return sigCalculoIndiretoInsumoDAO;
	}

	public SigCalculoIndiretoEquipamentoDAO getSigCalculoIndiretoEquipamentoDAO() {
		return sigCalculoIndiretoEquipamentoDAO;
	}

	public SigCalculoIndiretoPessoaDAO getSigCalculoIndiretoPessoaDAO() {
		return sigCalculoIndiretoPessoaDAO;
	}

	public SigCalculoIndiretoServicoDAO getSigCalculoIndiretoServicoDAO() {
		return sigCalculoIndiretoServicoDAO;
	}

	public SigCalculoClienteDAO getSigCalculoClienteDAO() {
		return sigCalculoClienteDAO;
	}
	
	public SigCalculoObjetoCustoDAO getSigCalculoObjetoCustoDAO() {
		return sigCalculoObjetoCustoDAO;
	}

	public SigCalculoRateioInsumoDAO getSigCalculoRateioInsumoDAO() {
		return sigCalculoRateioInsumoDAO;
	}

	public SigCalculoComponenteDAO getSigCalculoComponenteDAO() {
		return sigCalculoComponenteDAO;
	}

	public SigCalculoAtividadeInsumoDAO getSigCalculoAtividadeInsumoDAO() {
		return sigCalculoAtividadeInsumoDAO;
	}

	public SigCalculoAtdPacienteDAO getSigCalculoAtdPacienteDAO() {
		return sigCalculoAtdPacienteDAO;
	}
	
	public SigAtividadePessoaRestricoesDAO getSigAtividadePessoaRestricoesDAO() {
		return sigAtividadePessoaRestricoesDAO;
	}
}
