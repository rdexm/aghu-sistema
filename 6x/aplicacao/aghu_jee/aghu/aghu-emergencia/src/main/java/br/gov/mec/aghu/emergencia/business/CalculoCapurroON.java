package br.gov.mec.aghu.emergencia.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.vo.CalculoCapurroVO;
import br.gov.mec.aghu.model.McoIddGestCapurros;
import br.gov.mec.aghu.model.McoIddGestCapurrosId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestCapurrosDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

@Stateless
public class CalculoCapurroON extends BaseBusiness {

	private static final long serialVersionUID = -5191246321606647990L;

	@Inject
	private McoIddGestCapurrosDAO mcoIddGestCapurrosDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	public List<CalculoCapurroVO> listarCalculoCapurrosPorCodigoPaciente(Integer pacCodigo) {
		List<CalculoCapurroVO> listaRetorno = this.mcoIddGestCapurrosDAO.listarCalculoCapurrosPorCodigoPaciente(pacCodigo);
		
		for (CalculoCapurroVO vo : listaRetorno) {
			Servidor elaborador = registroColaboradorFacade.obterVRapPessoaServidorPorVinCodMatricula(vo.getSerMatricula(), vo.getSerVinCodigo());
			vo.setElaborador(elaborador);
			RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
			if (vo.getSerMatricula().equals(servidorLogado.getId().getMatricula())
					&& vo.getSerVinCodigo().equals(servidorLogado.getId().getVinCodigo())) {
				
				vo.setMesmoServidor(true);
			} else {
				vo.setMesmoServidor(false);
			}
		}
		return listaRetorno;
	}
	
	public boolean possuiAlteracaoIddGestCapurros(CalculoCapurroVO calculoSelecionado) {
		McoIddGestCapurrosId id = new McoIddGestCapurrosId(calculoSelecionado.getPacCodigo(),
				calculoSelecionado.getSerMatricula(), calculoSelecionado.getSerVinCodigo());
		McoIddGestCapurros iddGestCapurros = this.mcoIddGestCapurrosDAO.obterPorChavePrimaria(id);
		
		boolean alterado = false;
		
		if (CoreUtil.modificados(iddGestCapurros.getTexturaPele(), calculoSelecionado.getTexturaPele())
				|| CoreUtil.modificados(iddGestCapurros.getFormaOrelha(), calculoSelecionado.getFormaOrelha())
				|| CoreUtil.modificados(iddGestCapurros.getGlandulaMamaria(), calculoSelecionado.getGlandulaMamaria())
				|| CoreUtil.modificados(iddGestCapurros.getPregasPlantares(), calculoSelecionado.getPregasPlantares())
				|| CoreUtil.modificados(iddGestCapurros.getFormacaoMamilo(), calculoSelecionado.getFormacaoMamilo())
				|| CoreUtil.modificados(iddGestCapurros.getIgSemanas(), calculoSelecionado.getIgSemanas())
				|| CoreUtil.modificados(iddGestCapurros.getIgDias(), calculoSelecionado.getIgDias())) {
			
			alterado = true;
		}
		return alterado;
	}

}
