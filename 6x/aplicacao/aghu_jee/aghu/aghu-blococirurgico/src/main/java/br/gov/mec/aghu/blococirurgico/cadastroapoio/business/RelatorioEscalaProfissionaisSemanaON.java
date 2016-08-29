package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.RelatorioEscalaProfissionaisSemanaVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEscalaProfUnidCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcEscalaProfUnidCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @since 02/09/2013
 * @author jccouto (Jean Couto)
 * Estória #26899
 */
@Stateless
public class RelatorioEscalaProfissionaisSemanaON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(RelatorioEscalaProfissionaisSemanaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcEscalaProfUnidCirgDAO mbcEscalaProfUnidCirgDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 2396491815090457486L;
	
	public enum RelatorioEscalaProfissionaisSemanaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA,
		MENSAGEM_SALA_CIRURGICA_INATIVA;
	}
	
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO() {
		return mbcHorarioTurnoCirgDAO;
	}
	
	protected MbcEscalaProfUnidCirgDAO getMbcEscalaProfUnidCirgDAO() {
		return mbcEscalaProfUnidCirgDAO;
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO(){
		return mbcSalaCirurgicaDAO;
	}
	
	public String buscarUnidadeCirurgicaTurno(Short unidadeFuncional, String turno){

		String descricao = null;
		List<MbcHorarioTurnoCirg> unidadeTurno = getMbcHorarioTurnoCirgDAO().buscarHorariosCirgPorUnfSeqTurno(unidadeFuncional, turno);
		
		if(unidadeTurno != null){
			descricao = "Unid.: " + 
						unidadeTurno.get(0).getAghUnidadesFuncionais().getSeq() + 
						" - " +
						unidadeTurno.get(0).getAghUnidadesFuncionais().getDescricao() + 
						"    Turno: " +
						unidadeTurno.get(0).getMbcTurnos().getDescricao();
			
			return descricao;
		}

		return null;
	}

	public List<RelatorioEscalaProfissionaisSemanaVO> buscarDadosRelatorioEscalaProfissionaisSemana(AghUnidadesFuncionais unidadeFuncional, 
																									MbcTurnos turnos,
																									DominioFuncaoProfissional funcaoProfissional1,
																									DominioFuncaoProfissional funcaoProfissional2,
																									DominioFuncaoProfissional funcaoProfissional3,
																									DominioFuncaoProfissional funcaoProfissional4) throws ApplicationBusinessException {
		
		/* Lista com a Escala de Profissionais */
		List<MbcEscalaProfUnidCirg> escalaProfissionais = getMbcEscalaProfUnidCirgDAO().buscarDadosRelatorioEscalaProfUnidCirg(unidadeFuncional, 
																																turnos, 
																																funcaoProfissional1, 
													 																			funcaoProfissional2, 
													 																			funcaoProfissional3, 
													 																			funcaoProfissional4);
		
		if(escalaProfissionais.isEmpty()){
			throw new ApplicationBusinessException(RelatorioEscalaProfissionaisSemanaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		/* Lista com as Salas Cirúrgicas */
		List<MbcSalaCirurgica> salaCirurgicas = getMbcSalaCirurgicaDAO().buscarSalasCirurgicasPorUnfSeq(unidadeFuncional.getSeq());
		
		if(salaCirurgicas.isEmpty()){
			throw new ApplicationBusinessException(RelatorioEscalaProfissionaisSemanaONExceptionCode.MENSAGEM_SALA_CIRURGICA_INATIVA);
		}
		
		return this.buscarDadosRelatorioVO(escalaProfissionais, salaCirurgicas);
	}
	
	private List<RelatorioEscalaProfissionaisSemanaVO> buscarDadosRelatorioVO(List<MbcEscalaProfUnidCirg> escalaProfissionais, List<MbcSalaCirurgica> salaCirurgicas){
		
		/* Lista do VO de retorno */
		List<RelatorioEscalaProfissionaisSemanaVO> relatorioEscalaProfissionais = new ArrayList<RelatorioEscalaProfissionaisSemanaVO>();
		
		for (MbcSalaCirurgica salas : salaCirurgicas) {
			RelatorioEscalaProfissionaisSemanaVO voEscalas = new RelatorioEscalaProfissionaisSemanaVO();
			voEscalas.setNrSala(salas.getId().getSeqp());
			
			for (MbcEscalaProfUnidCirg escalas : escalaProfissionais) {
				
				if(salas.getId().getSeqp() == escalas.getMbcCaracteristicaSalaCirg().getMbcSalaCirurgica().getId().getSeqp()){
					
					DominioDiaSemana diaSemana = escalas.getMbcCaracteristicaSalaCirg().getDiaSemana();
					switch (diaSemana) {
					case SEG:
						
						if(voEscalas.getFuncaoNomeSeg1() == null){
							voEscalas.setFuncaoNomeSeg1(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeSeg2() == null){
							voEscalas.setFuncaoNomeSeg2(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeSeg3() == null){
							voEscalas.setFuncaoNomeSeg3(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else {
							voEscalas.setFuncaoNomeSeg4(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						}
						
						break;
						
					case TER:
						
						if(voEscalas.getFuncaoNomeTer1() == null){
							voEscalas.setFuncaoNomeTer1(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeTer2() == null){
							voEscalas.setFuncaoNomeTer2(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeTer3() == null){
							voEscalas.setFuncaoNomeTer3(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else {
							voEscalas.setFuncaoNomeTer4(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						}

						break;
						
					case QUA:
						
						if(voEscalas.getFuncaoNomeQua1() == null){
							voEscalas.setFuncaoNomeQua1(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeQua2() == null){
							voEscalas.setFuncaoNomeQua2(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeQua3() == null){
							voEscalas.setFuncaoNomeQua3(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else {
							voEscalas.setFuncaoNomeQua4(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						}
						
						break;
					
					case QUI:
						
						if(voEscalas.getFuncaoNomeQui1() == null){
							voEscalas.setFuncaoNomeQui1(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeQui2() == null){
							voEscalas.setFuncaoNomeQui2(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeQui3() == null){
							voEscalas.setFuncaoNomeQui3(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else {
							voEscalas.setFuncaoNomeQui4(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						}
						
						break;
						
					case SEX:
						
						if(voEscalas.getFuncaoNomeSex1() == null){
							voEscalas.setFuncaoNomeSex1(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeSex2() == null){
							voEscalas.setFuncaoNomeSex2(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeSex3() == null){
							voEscalas.setFuncaoNomeSex3(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else {
							voEscalas.setFuncaoNomeSex4(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						}
						
						break;
					
					case SAB:
						
						if(voEscalas.getFuncaoNomeSab1() == null){
							voEscalas.setFuncaoNomeSab1(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeSab2() == null){
							voEscalas.setFuncaoNomeSab2(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else if(voEscalas.getFuncaoNomeSab3() == null){
							voEscalas.setFuncaoNomeSab3(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						} else {
							voEscalas.setFuncaoNomeSab4(escalas.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf().getCodigo() +"-"+ escalas.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
						}
						
						break;

					default:
						break;
					}

				}
			}
			
			relatorioEscalaProfissionais.add(voEscalas);
		}
		
		return relatorioEscalaProfissionais;
		
	}

}
