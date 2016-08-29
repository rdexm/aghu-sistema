package br.gov.mec.aghu.blococirurgico.cedenciasala.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class BloqueioSalaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(BloqueioSalaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcBloqSalaCirurgicaDAO mbcBloqSalaCirurgicaDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@EJB
	private BloqueioSalaRN bloqueioSalaRN;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade iBlocoCirurgicoCadastroApoioFacade;

	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade iBlocoCirurgicoCedenciaSalaFacade;
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = -1943606156045895400L;		
	
	
	public List<MbcBloqSalaCirurgica> pesquisarBloqSalaCirurgica(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana, String turno,
			Short vinCodigo, Integer matricula, Short especialidade) {		
		List<MbcBloqSalaCirurgica> listasalas = getMbcBloqSalaCirurgicaDAO().pesquisarBloqSalaCirurgica(firstResult, maxResult, orderProperty, asc,
				unfSigla, seqp, dtInicio, dtFim, diaSemana, turno, vinCodigo, matricula, especialidade);
		
//		int count = 0;
//		MbcBloqSalaCirurgica salaTemp = new MbcBloqSalaCirurgica();
		for (MbcBloqSalaCirurgica sala : listasalas) {
			
			if(sala.getEspecialidade() != null){
				sala.setEspecialidade(aghEspecialidadesDAO.obterPorChavePrimaria(sala.getEspecialidade().getSeq()));
			
			}
			
			if (sala.getMbcSalaCirurgica().getUnidadeFuncional() != null){
			    sala.setUnidadeSalaCirurgica(iAghuFacade.obterUnidadeFuncional(sala.getMbcSalaCirurgica().getUnidadeFuncional().getSeq()));
			}
			
			if(sala.getMbcSalaCirurgica() !=null){
			   sala.setMbcSalaCirurgica(iBlocoCirurgicoCadastroApoioFacade.obterSalaCirurgicaBySalaCirurgicaId(sala.getMbcSalaCirurgica().getId().getSeqp(), sala.getMbcSalaCirurgica().getId().getUnfSeq()));
			}

			if (sala.getTurno() != null) {
			   sala.setTurno(iBlocoCirurgicoCadastroApoioFacade.obterMbcTurnodById(sala.getTurno().getTurno()));
			}
		
			if (sala.getMbcProfAtuaUnidCirgs() != null){
			    sala.setMbcProfAtuaUnidCirgs(mbcProfAtuaUnidCirgsDAO.obterPorChavePrimaria(sala.getMbcProfAtuaUnidCirgs().getId()));
			    
				if (sala.getMbcProfAtuaUnidCirgs().getRapServidores() != null) {
				    sala.getMbcProfAtuaUnidCirgs().setRapServidores(iRegistroColaboradorFacade.obterRapServidor(sala.getMbcProfAtuaUnidCirgs().getRapServidores().getId()));
				    
					try {
						if (sala.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica() != null){
						   sala.getMbcProfAtuaUnidCirgs().getRapServidores().setPessoaFisica(iRegistroColaboradorFacade.obterPessoaFisica(sala.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getCodigo()));
						}
					} catch (ApplicationBusinessException e) {
						LOG.error("PESSOA FISICA NAO ENCONTRADA");
					}
				}
			}
			
		}
		
		return listasalas;
	}	

	public Long pesquisarBloqSalaCirurgicaCount(String unfSigla, Short seqp, Date dtInicio, Date dtFim, DominioDiaSemanaSigla diaSemana,
			String turno, Short vinCodigo, Integer matricula, Short especialidade) {
		return getMbcBloqSalaCirurgicaDAO().pesquisarBloqSalaCirurgicaCount(unfSigla, seqp, dtInicio, dtFim, diaSemana, turno, vinCodigo, matricula, especialidade);
	}

	public void atualizarMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgicaSelecionado) throws ApplicationBusinessException {
		
		getBloqueioSalaRN().setarMbcBloqSalaCirurgicaServidorLogadoECriadoEm(bloqueioSalaCirurgicaSelecionado);		
		bloqueioSalaCirurgicaSelecionado.setIndSituacao(bloqueioSalaCirurgicaSelecionado.getIndSituacao().equals(DominioSituacao.I)?DominioSituacao.A:DominioSituacao.I);
		getMbcBloqSalaCirurgicaDAO().atualizar(bloqueioSalaCirurgicaSelecionado);			
	}

	public String gravarMbcBloqSalaCirurgica(MbcBloqSalaCirurgica bloqueioSalaCirurgica, LinhaReportVO equipe) throws ApplicationBusinessException {
		
		getBloqueioSalaRN().executarAntesInserir(bloqueioSalaCirurgica);		
		
		if(equipe != null){
			MbcProfAtuaUnidCirgs puc = new MbcProfAtuaUnidCirgs();
			puc.setId(new MbcProfAtuaUnidCirgsId(equipe.getNumero11().intValue(),equipe.getNumero4(),equipe.getNumero5(), (DominioFuncaoProfissional)equipe.getObject()));	 
			bloqueioSalaCirurgica.setMbcProfAtuaUnidCirgs(getBlocoCirurgicoCadastroApoioFacade().obterMbcProfAtuaUnidCirgsPorId(puc.getId()));
			bloqueioSalaCirurgica.setEspecialidade(aghEspecialidadesDAO.obterPorChavePrimaria(equipe.getSeqEsp()));
		}
		
		if (bloqueioSalaCirurgica.getDtInicio().equals(bloqueioSalaCirurgica.getDtFim())) {
			Calendar cal = Calendar.getInstance(); // hoje
			cal.setTime(bloqueioSalaCirurgica.getDtInicio()); // uma Date
			int diaSemana = cal.get(Calendar.DAY_OF_WEEK);

			bloqueioSalaCirurgica.setDiaSemana(DominioDiaSemanaSigla.getDiaSemanaSigla(diaSemana));
		}
	    
		getBlocoCirurgicoCedenciaSalaFacade().persistirMbcBloqSalaCirurgica(bloqueioSalaCirurgica);
	    
		return "MENSAGEM_BLOQUEIO_SALA_INSERCAO_COM_SUCESSO";	
	}
	
	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais) {
		return pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, true);
	}
	
	public List<LinhaReportVO> pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais, boolean matriculaLong) {
		List <LinhaReportVO> listaLinhaReportVO = getMbcProfAtuaUnidCirgsDAO().pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgs(pesquisaSuggestion, unfSeq, sciSeqp,
				dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, matriculaLong);
		return listaLinhaReportVO;
	}

	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais) {
		
		return pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisaSuggestion, unfSeq, sciSeqp, dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, true);
	}

	public Long pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(Object pesquisaSuggestion, Short unfSeq, Short sciSeqp,
			DominioDiaSemanaSigla dominioDiaSemanaSigla, String turno, DominioSituacao situacao, DominioFuncaoProfissional[] funcoesProfissionais, boolean matriculaLong) {
		
		return getMbcProfAtuaUnidCirgsDAO().pesquisarNomeMatVinCodUnfByMbcProfAtuaUnidCirgsCount(pesquisaSuggestion, unfSeq, sciSeqp,
				dominioDiaSemanaSigla, turno, situacao, funcoesProfissionais, matriculaLong);
	}
	
	protected IBlocoCirurgicoCedenciaSalaFacade getBlocoCirurgicoCedenciaSalaFacade() {
		return iBlocoCirurgicoCedenciaSalaFacade;
	}
	
	protected IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return iBlocoCirurgicoCadastroApoioFacade;
	}
	
	protected MbcBloqSalaCirurgicaDAO getMbcBloqSalaCirurgicaDAO() {
		return mbcBloqSalaCirurgicaDAO;
	}
	
	protected BloqueioSalaRN getBloqueioSalaRN() {
		return bloqueioSalaRN;
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}
	
		
}