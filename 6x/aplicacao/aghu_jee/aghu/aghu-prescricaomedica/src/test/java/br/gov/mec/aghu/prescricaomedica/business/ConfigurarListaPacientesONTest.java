package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.MpmListaServEquipe;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaPacCpaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServEquipeDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServEspecialidadeDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPacAtendProfissionalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmServidorUnidFuncionalDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe
 * ConfigurarListaPacientesON.<br>
 * Criado com base no teste {@link VerificarPrescricaoONTest}.
 * 
 * @author cvagheti
 * 
 */
public class ConfigurarListaPacientesONTest extends AGHUBaseUnitTest<ConfigurarListaPacientesON>{

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private MpmListaPacCpaDAO mockedMpmListaPacCpasDAO;
	@Mock
	private MpmPacAtendProfissionalDAO mockedMpmPacAtendProfissionaisDAO;
	@Mock
	private MpmServidorUnidFuncionalDAO mockedMpmServidorUnidFuncionaisDAO;
	@Mock
	private MpmListaServEspecialidadeDAO mockedMpmListaServEspecialidadesDAO;
	@Mock
	private MpmListaServEquipeDAO mockedMpmListaServEquipesDAO;

	@Test
	public void salvarListaEspecialidadesTest() {

		try {
			RapServidores servidor = new RapServidores(new RapServidoresId(1,
					(short) 1));
			MpmListaServEspecialidade mpmListaServEspecialidade = new MpmListaServEspecialidade();
			AghEspecialidades especialidade = new AghEspecialidades();
			especialidade.setSeq((short)1);
			especialidade.setSigla("AAA");
			mpmListaServEspecialidade.setEspecialidade(especialidade);
			mpmListaServEspecialidade.setServidor(servidor);
			final List<MpmListaServEspecialidade> listaServEspecialidades = new ArrayList<MpmListaServEspecialidade>();
			listaServEspecialidades.add(mpmListaServEspecialidade);

			List<AghEspecialidades> listaServEspecialidadesInserir = new ArrayList<AghEspecialidades>();
			final AghEspecialidades especialidade2 = new AghEspecialidades();
			especialidade2.setSeq((short)2);
			especialidade2.setSigla("BBB");
			listaServEspecialidadesInserir.add(especialidade2);
			
			Mockito.when(mockedMpmListaServEspecialidadesDAO.pesquisarAssociacoesPorServidor(Mockito.any(RapServidores.class))).
			thenReturn(listaServEspecialidades);
			
			systemUnderTest.salvarListaEspecialidades(listaServEspecialidadesInserir, servidor);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void salvarListaEquipesTest() {

		try {
			RapServidores servidor = new RapServidores(new RapServidoresId(1,
					(short) 1));
			MpmListaServEquipe mpmListaServEquipe = new MpmListaServEquipe();
			AghEquipes equipe = new AghEquipes();
			equipe.setSeq(1);
			mpmListaServEquipe.setEquipe(equipe);
			mpmListaServEquipe.setServidor(servidor);
			final List<MpmListaServEquipe> listaServEquipes = new ArrayList<MpmListaServEquipe>();
			listaServEquipes.add(mpmListaServEquipe);

			List<AghEquipes> listaServEquipesInserir = new ArrayList<AghEquipes>();
			final AghEquipes equipe2 = new AghEquipes();
			equipe2.setSeq(2);
			listaServEquipesInserir.add(equipe2);
			
			Mockito.when(mockedMpmListaServEquipesDAO.pesquisarAssociacoesPorServidor(Mockito.any(RapServidores.class))).
			thenReturn(listaServEquipes);
			
			systemUnderTest.salvarListaEquipes(listaServEquipesInserir, servidor);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void salvarListaUnidadesTest() {

		try {
			RapServidores servidor = new RapServidores(new RapServidoresId(1,
					(short) 1));
			MpmServidorUnidFuncional mpmListaServidorUnidFuncional = new MpmServidorUnidFuncional();
			AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
			unidade.setSeq((short)1);
			mpmListaServidorUnidFuncional.setUnidadeFuncional(unidade);
			mpmListaServidorUnidFuncional.setServidor(servidor);
			final List<MpmServidorUnidFuncional> listaServidorUnidFuncionals = new ArrayList<MpmServidorUnidFuncional>();
			listaServidorUnidFuncionals.add(mpmListaServidorUnidFuncional);

			List<AghUnidadesFuncionais> listaServUnidadesInserir = new ArrayList<AghUnidadesFuncionais>();
			final AghUnidadesFuncionais unidade2 = new AghUnidadesFuncionais();
			unidade2.setSeq((short) 2);
			listaServUnidadesInserir.add(unidade2);
			
			Mockito.when(mockedMpmServidorUnidFuncionaisDAO.pesquisarAssociacoesPorServidor(Mockito.any(RapServidores.class))).
			thenReturn(listaServidorUnidFuncionals);
			
			systemUnderTest.salvarListaUnidadesFuncionais(listaServUnidadesInserir, servidor);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void salvarListaAtendimentosTest() {

		try {
			RapServidores servidor = new RapServidores(new RapServidoresId(1,
					(short) 1));
			MpmPacAtendProfissional mpmPacAtendProfissional = new MpmPacAtendProfissional();
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setSeq((int)1);
			mpmPacAtendProfissional.setAtendimento(atendimento);
			mpmPacAtendProfissional.setServidor(servidor);
			final List<MpmPacAtendProfissional> listaServidorAtendimentos = new ArrayList<MpmPacAtendProfissional>();
			listaServidorAtendimentos.add(mpmPacAtendProfissional);

			List<AghAtendimentos> listaServAtendimentosInserir = new ArrayList<AghAtendimentos>();
			final AghAtendimentos atendimento2 = new AghAtendimentos();
			atendimento2.setSeq(2);
			listaServAtendimentosInserir.add(atendimento2);
			
			Mockito.when(mockedMpmPacAtendProfissionaisDAO.pesquisarAssociacoesPorServidor(Mockito.any(RapServidores.class))).
			thenReturn(listaServidorAtendimentos);

			systemUnderTest.salvarListaAtendimentos(listaServAtendimentosInserir, servidor);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void salvarIndicePacientesAtendimentosTest() {

		try {
			final RapServidores servidor = new RapServidores(new RapServidoresId(1,
					(short) 1));
			boolean emAtendimento = true;
			final MpmListaPacCpa cpa = null;
			
			Mockito.when(mockedMpmListaPacCpasDAO.busca(Mockito.any(RapServidores.class))).
			thenReturn(cpa);
			
			systemUnderTest.salvarIndicadorPacientesAtendimento(emAtendimento, servidor);
			
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
}
