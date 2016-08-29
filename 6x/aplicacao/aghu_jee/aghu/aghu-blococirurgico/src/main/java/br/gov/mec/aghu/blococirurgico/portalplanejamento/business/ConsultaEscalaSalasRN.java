package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasBuscaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasDiasVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasEquipeVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasProfisionaisVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasProfissionalEspecialidadeVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaSalasVO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ConsultaEscalaSalasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaEscalaSalasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;


	@EJB
	private IPesquisaInternacaoFacade iPesquisaInternacaoFacade;
	private static final long	serialVersionUID	= -3860310363916272851L;

	@SuppressWarnings("deprecation")
	public List<EscalaSalasVO> pesquisarEscalaSalasPorUnidadeCirurgica(final Short unfSeq) {
		final List<EscalaSalasVO> ret = new ArrayList<EscalaSalasVO>();
		List<EscalaSalasBuscaVO> dados = this.getMbcSalaCirurgicaDAO().buscarEscalaSalasPorUnidadeCirurgica(unfSeq);
		EscalaSalasVO novo = new EscalaSalasVO();
		IPesquisaInternacaoFacade pesquisaInternacaoFacade = getPesquisaInternacaoFacade();
		for (EscalaSalasBuscaVO escalaSalasBuscaVO : dados) {
			if(novo.getSala() == null || novo.getSala().intValue() != escalaSalasBuscaVO.getSeqp().intValue()){
				if(novo.getSala() != null ){
					ret.add(novo);
					novo = new EscalaSalasVO();
				}
				novo.setSala(escalaSalasBuscaVO.getSeqp());
				novo.setTurnos(new ArrayList<EscalaSalasDiasVO>());
			}
			if(novo.getTurnos().isEmpty() || !novo.getTurnos().get(novo.getTurnos().size()-1).getTurno().equals(escalaSalasBuscaVO.getTurno())) {
				EscalaSalasDiasVO novaEscala = new EscalaSalasDiasVO();
				novaEscala.setTurno(escalaSalasBuscaVO.getTurno());
				novaEscala.setDias(new EscalaSalasEquipeVO[]{new EscalaSalasEquipeVO(), new EscalaSalasEquipeVO(), new EscalaSalasEquipeVO(), new EscalaSalasEquipeVO(), new EscalaSalasEquipeVO(), new EscalaSalasEquipeVO(), new EscalaSalasEquipeVO()});
				novo.getTurnos().add(novaEscala);
			}
			final EscalaSalasDiasVO escala = novo.getTurnos().get(novo.getTurnos().size()-1);
			escala.getDias()[escalaSalasBuscaVO.getOrdem().intValue()].setParticular(escalaSalasBuscaVO.getParticular());
			escala.getDias()[escalaSalasBuscaVO.getOrdem().intValue()].setUrgencia(escalaSalasBuscaVO.getUrgencia());
//			escala.getDias()[escalaSalasBuscaVO.getOrdem()].setDiaSemana(escalaSalasBuscaVO.getDiasemana());
			
			final List<EscalaSalasProfissionalEspecialidadeVO> profs = this.getMbcSalaCirurgicaDAO().buscarProfissionalEscala(unfSeq, novo.getSala(),
					escala.getTurno(), escalaSalasBuscaVO.getDiasemana());
			if (profs != null && !profs.isEmpty()) {
				escala.getDias()[escalaSalasBuscaVO.getOrdem().intValue()].setProfissionais(new ArrayList<EscalaSalasProfisionaisVO>());
				for (EscalaSalasProfissionalEspecialidadeVO escalaSalasProfissionalEspecialidadeVO : profs) {
					final String nome = pesquisaInternacaoFacade.buscarNomeUsual(escalaSalasProfissionalEspecialidadeVO.getCodigo(),
							escalaSalasProfissionalEspecialidadeVO.getMatricula());
					if (nome != null) {
						final EscalaSalasProfisionaisVO escalaSalasProfisionaisVO = new EscalaSalasProfisionaisVO();
						escalaSalasProfisionaisVO.setNome(WordUtils.capitalize(nome.toLowerCase()));
						escalaSalasProfisionaisVO.setSiglaEspecialidade(escalaSalasProfissionalEspecialidadeVO.getSigla());
						escala.getDias()[escalaSalasBuscaVO.getOrdem()].getProfissionais().add(escalaSalasProfisionaisVO);
					}
				}
//				Ordenação de especialidade e profissional. Feito por comparator pois o nome pode ser tanto nome como nome usual 
//				e teríamos que mudar toda lógica da estória já entregue.
				Collections.sort(escala.getDias()[escalaSalasBuscaVO.getOrdem()].getProfissionais(), new Comparator<EscalaSalasProfisionaisVO>() {
					@Override
					public int compare(EscalaSalasProfisionaisVO o1, EscalaSalasProfisionaisVO o2) {
						if (o1.getSiglaEspecialidade().compareTo(o2.getSiglaEspecialidade()) == 0) {
							return o1.getNome().compareTo(o2.getNome());
						} else {
							return o1.getSiglaEspecialidade().compareTo(o2.getSiglaEspecialidade());
						}
					}
				});
			}
		}
		//Se entrou no laço anterior, não adicionou a ultima sala. Adiciona neste momento:
		if (dados != null && !dados.isEmpty()) {
			ret.add(novo);
		}
		
		return ret;
	}

	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO() {
		return mbcSalaCirurgicaDAO;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return this.iPesquisaInternacaoFacade;
	}

}