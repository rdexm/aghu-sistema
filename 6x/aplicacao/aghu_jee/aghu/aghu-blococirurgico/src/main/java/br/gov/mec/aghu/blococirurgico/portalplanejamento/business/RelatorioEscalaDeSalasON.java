package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasLinhaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class RelatorioEscalaDeSalasON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioEscalaDeSalasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;


	@EJB
	private RelatorioEscalaDeSalasRN relatorioEscalaDeSalasRN;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = -2082099119867465475L;
	
	protected enum RelatorioEscalaDeSalasONExceptionCode implements BusinessExceptionCode {
		MBC_00942
	}

	public List<RelatorioEscalaDeSalasVO> listarEquipeSalas(Short seqUnidade) {
		AghUnidadesFuncionais unidade = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidade); //c1
		List<MbcCaracteristicaSalaCirg> listSalas = getMbcCaracteristicaSalaCirgDAO().listarCaracteristicaSalaCirgPorUnidade(seqUnidade, DominioSituacao.A); //c2
		List<RelatorioEscalaDeSalasVO> listaVo = new ArrayList<RelatorioEscalaDeSalasVO>();
		
		RelatorioEscalaDeSalasRN escalaDeSalaRn = getRelatorioEscalaDeSalasRN();
		List<RelatorioEscalaDeSalasLinhaVO> listLinha = new ArrayList<RelatorioEscalaDeSalasLinhaVO>();//Lista de linhas de uma sala
		for (MbcCaracteristicaSalaCirg sala : listSalas){
			if(listaVo.isEmpty()){ //Primeira vez
				RelatorioEscalaDeSalasVO vo = new RelatorioEscalaDeSalasVO();
				
				vo.setNomeHospital(getAghuFacade().getRazaoSocial());
				vo.setDataAtual(new Date());
				vo.setDescricaoUnidade(unidade.getDescricao());
				vo.setSala(processarSala(sala.getMbcSalaCirurgica().getId().getSeqp()));			
				listaVo.add(vo);
			}else{
				if(!processarSala(sala.getMbcSalaCirurgica().getId().getSeqp()).equals(listaVo.get(listaVo.size()-1).getSala())){
					listaVo.get(listaVo.size()-1).setLinha(listLinha);
					listLinha = new ArrayList<RelatorioEscalaDeSalasLinhaVO>();

					RelatorioEscalaDeSalasVO vo = new RelatorioEscalaDeSalasVO();
					
					vo.setNomeHospital(getAghuFacade().getRazaoSocial());
					vo.setDataAtual(new Date());
					vo.setDescricaoUnidade(unidade.getDescricao());
					vo.setSala(processarSala(sala.getMbcSalaCirurgica().getId().getSeqp()));			
					listaVo.add(vo);
				}
			}
			RelatorioEscalaDeSalasLinhaVO linha = processarLinha(seqUnidade, escalaDeSalaRn, sala);
			if(!listLinha.contains(linha)){// distinct
				listLinha.add(linha);
			}			
		}
		listaVo.get(listaVo.size()-1).setLinha(listLinha);//add a ultima lista na ultima linha
		
		return listaVo;
	}

	private RelatorioEscalaDeSalasLinhaVO processarLinha(Short seqUnidade, RelatorioEscalaDeSalasRN escalaDeSalaRn,
			MbcCaracteristicaSalaCirg sala) {
		MbcTurnos turno = sala.getMbcHorarioTurnoCirg().getMbcTurnos();
		Short seqpSala = sala.getMbcSalaCirurgica().getId().getSeqp();
		RelatorioEscalaDeSalasLinhaVO linhaVo = new RelatorioEscalaDeSalasLinhaVO();
		
		linhaVo.setTurno(turno.getTurno());
		linhaVo.setSeg(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.SEG));
		linhaVo.setTer(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.TER));
		linhaVo.setQua(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.QUA));
		linhaVo.setQui(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.QUI));
		linhaVo.setSex(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.SEX));
		linhaVo.setSab(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.SAB));
		linhaVo.setDom(escalaDeSalaRn.obterEquipeSala(seqUnidade, seqpSala, turno, DominioDiaSemana.DOM));
		return linhaVo;
	}

	private String processarSala(Short seqp) {
		if(seqp == null){
			return "";
		}
		return "Sala ".concat(seqp.toString());
	}
	
	/**
	 * Valida se a situação é diferente de AG <br>
	 * Se for, impede a transferencia do paciente e informa ao usuário. #40203
	 * @param agenda
	 * @throws AGHUNegocioException
	 */
	public void validaSituacaoAgenda(MbcAgendas agenda) throws ApplicationBusinessException{
		if(!DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())){
			throw new ApplicationBusinessException(RelatorioEscalaDeSalasONExceptionCode.MBC_00942, Severity.WARN);
		}
	}

	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected RelatorioEscalaDeSalasRN getRelatorioEscalaDeSalasRN() {
		return relatorioEscalaDeSalasRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
}